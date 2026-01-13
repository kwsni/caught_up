package com.kwsni.caught_up.config;

import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.LimitCheckingExceptionHierarchySkipPolicy;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.batch.TimestampJobExecutionListener;
import com.kwsni.caught_up.tvdb.batch.EpisodeProcessor;
import com.kwsni.caught_up.tvdb.batch.InvalidRecordException;
import com.kwsni.caught_up.tvdb.batch.SeriesEpisodeItemReader;
import com.kwsni.caught_up.tvdb.batch.SeriesPagingItemReader;
import com.kwsni.caught_up.tvdb.batch.SeriesProcessor;
import com.kwsni.caught_up.tvdb.batch.UpdatePagingItemReader;
import com.kwsni.caught_up.tvdb.batch.UpdateProcessor;
import com.kwsni.caught_up.tvdb.batch.UpdateWriter;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecord;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.Update;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.EpisodeRepository;
import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

import jakarta.persistence.EntityManager;

@Configuration
public class TvdbBatchJobConfig {
    private DataSource dataSource;
    private EntityManager entityManager;
    private RestClient tvdbClient;
    private RedisTemplate<String, String> redisTemplate;

    TvdbBatchJobConfig(
        DataSource dataSource,
        EntityManager entityManager,
        RestClient tvdbClient,
        RedisTemplate<String, String> redisTemplate
    ) {
        this.entityManager = entityManager;
        this.dataSource = dataSource;
        this.tvdbClient = tvdbClient;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    @StepScope
    SeriesPagingItemReader seriesReader() {
        return new SeriesPagingItemReader(tvdbClient);
    }

    @Bean
    @StepScope
    SeriesEpisodeItemReader episodeReader() throws Exception {
        return new SeriesEpisodeItemReader(dbSeriesReader(), tvdbClient);
    }

    @Bean
    @StepScope
    JdbcPagingItemReader<Integer> dbSeriesReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<Integer>()
            .name("dbSeriesReader")
            .dataSource(dataSource)
            .selectClause("tvdb_id")
            .fromClause("series")
            .sortKeys(Map.of("tvdb_id", Order.ASCENDING))
            .rowMapper(new SingleColumnRowMapper<>(Integer.class))
            .pageSize(500)
            .build();
    }

    @Bean
    @StepScope
    SeriesProcessor seriesProcessor() {
        return new SeriesProcessor(tvdbClient);
    }

    @Bean
    @StepScope
    EpisodeProcessor episodeProcessor() {
        return new EpisodeProcessor(entityManager);
    }

    @Bean
    @StepScope
    RepositoryItemWriter<Series> seriesWriter(SeriesRepository seriesRepository) {
        return new RepositoryItemWriterBuilder<Series>()
            .repository(seriesRepository)
            .build();
    }

    @Bean
    @StepScope
    RepositoryItemWriter<Episode> episodeWriter(EpisodeRepository episodeRepository) {
        return new RepositoryItemWriterBuilder<Episode>()
            .repository(episodeRepository)
            .build();
    }

    @Bean
    @StepScope
    UpdatePagingItemReader updateReader() {
        return new UpdatePagingItemReader(tvdbClient, redisTemplate);
    }

    @Bean
    @StepScope
    UpdateProcessor updateProcessor() {
        return new UpdateProcessor(entityManager, tvdbClient);
    }

    @Bean
    @StepScope
    UpdateWriter updateWriter(SeriesRepository seriesRepository,
        EpisodeRepository episodeRepository
    ) {
        return new UpdateWriter(
            seriesWriter(seriesRepository),
            episodeWriter(episodeRepository),
            seriesRepository,
            episodeRepository,
            entityManager
        );
    }

    @Bean
    TimestampJobExecutionListener timestampJobExecutionListener() {
        return new TimestampJobExecutionListener(redisTemplate);
    }

    @Bean
    Job tvdbInitialSyncJob(JobRepository jobRepository,
        TimestampJobExecutionListener timestampJobExecutionListener,
        Step seriesStep,
        Step episodeStep
    ) {
        return new JobBuilder("tvdbInitialSyncJob", jobRepository)
            .listener(timestampJobExecutionListener)
            .start(seriesStep)
            .next(episodeStep)
            .build();
    }

    @Bean
    Job tvdbUpdateSyncJob(JobRepository jobRepository,
        Step updateStep
    ) {
        return new JobBuilder("tvdbUpdateSyncJob", jobRepository)
            .start(updateStep)
            .build();
    }

    @Bean
    Step seriesStep(JobRepository jobRepository,
        SeriesPagingItemReader seriesReader,
        SeriesProcessor seriesProcessor,
        RepositoryItemWriter<Series> seriesWriter
    ) {
        return new StepBuilder("initializeSeries", jobRepository)
            .<SeriesBaseRecordDto, Series>chunk(500)
            .transactionManager(transactionManager())
            .reader(seriesReader)
            .processor(seriesProcessor)
            .writer(seriesWriter)
            .build();
    }

    @Bean
    Step episodeStep(JobRepository jobRepository,
        SeriesEpisodeItemReader episodeReader,
        EpisodeProcessor episodeProcessor,
        RepositoryItemWriter<Episode> episodeWriter
    ) {
        return new StepBuilder("initializeEpisode", jobRepository)
            .<EpisodeBaseRecordDto, Episode>chunk(500)
            .transactionManager(transactionManager())
            .reader(episodeReader)
            .processor(episodeProcessor)
            .writer(episodeWriter)
            .faultTolerant()
            .skipPolicy(new LimitCheckingExceptionHierarchySkipPolicy(
                Set.of(Exception.class), 10))
            .build();
    }

    @Bean
    Step updateStep(JobRepository jobRepository,
        UpdatePagingItemReader updateReader,
        UpdateProcessor updateProcessor,
        UpdateWriter updateWriter
    ) {
        return new StepBuilder("updateStep", jobRepository)
            .<Update, UpdateRecord>chunk(500)
            .transactionManager(transactionManager())
            .reader(updateReader)
            .processor(updateProcessor)
            .writer(updateWriter)
            .faultTolerant()
            .skipPolicy((throwable, count) -> 
                throwable.getClass() == InvalidRecordException.class
            )
            .build();
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
}
