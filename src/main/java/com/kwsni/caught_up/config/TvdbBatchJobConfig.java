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
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.batch.EpisodeProcessor;
import com.kwsni.caught_up.tvdb.batch.InvalidRecordException;
import com.kwsni.caught_up.tvdb.batch.SeriesEpisodeItemReader;
import com.kwsni.caught_up.tvdb.batch.SeriesPagingItemReader;
import com.kwsni.caught_up.tvdb.batch.SeriesProcessor;
import com.kwsni.caught_up.tvdb.batch.TimestampJobExecutionListener;
import com.kwsni.caught_up.tvdb.batch.UpdateJobExecutionListener;
import com.kwsni.caught_up.tvdb.batch.UpdatePagingItemReader;
import com.kwsni.caught_up.tvdb.batch.UpdateProcessor;
import com.kwsni.caught_up.tvdb.batch.UpdateRecordProcessor;
import com.kwsni.caught_up.tvdb.batch.UpdateRecordWriter;
import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.batch.repository.UpdateRecordRepository;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.UpdateDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

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
    UpdatePagingItemReader updateReader() {
        return new UpdatePagingItemReader(tvdbClient, redisTemplate);
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
    RepositoryItemReader<UpdateRecord> updateRecordReader(UpdateRecordRepository updateRecordRepository) {
        return new RepositoryItemReaderBuilder<UpdateRecord>()
            .repository(updateRecordRepository)
            .methodName("findAll")
            .pageSize(500)
            .sorts(Map.of("recordId", Direction.ASC, "entityType", Direction.ASC))
            .saveState(false)
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
    UpdateProcessor updateProcessor() {
        return new UpdateProcessor();
    }

    @Bean
    @StepScope
    UpdateRecordProcessor updateRecordProcessor() {
        return new UpdateRecordProcessor(entityManager, tvdbClient);
    }

    @Bean
    @StepScope
    RepositoryItemWriter<UpdateRecord> updateWriter(UpdateRecordRepository updateRecordRepository) {
        return new RepositoryItemWriterBuilder<UpdateRecord>()
            .repository(updateRecordRepository)
            .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Series> seriesWriter() {
        return new JdbcBatchItemWriterBuilder<Series>()
            .dataSource(dataSource)
            .sql("INSERT INTO series (tvdb_id, name, year, first_aired, last_aired, next_aired, score, image, overview, country, last_updated, slug) VALUES (:tvdbId, :name, :year, :firstAired, :lastAired, :nextAired, :score, :image, :overview, :country, :lastUpdated, :slug) ON CONFLICT (tvdb_id) DO UPDATE SET name = EXCLUDED.name, year = EXCLUDED.year, first_aired = EXCLUDED.first_aired, last_aired = EXCLUDED.last_aired, next_aired = EXCLUDED.next_aired, score = EXCLUDED.score, image = EXCLUDED.image, overview = EXCLUDED.overview, country = EXCLUDED.country, last_updated = EXCLUDED.last_updated")
            .beanMapped()
            .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Episode> episodeWriter() {
        return new JdbcBatchItemWriterBuilder<Episode>()
            .dataSource(dataSource)
            .sql("INSERT INTO episode (tvdb_id, name, series_tvdb_id, season_number, season_name, airs_after_season, airs_before_episode, airs_before_season, number, absolute_number, runtime, aired, year, image, image_type, overview, is_movie) VALUES (:tvdbId, :name, :series.tvdbId, :seasonNumber, :seasonName, :airsAfterSeason, :airsBeforeEpisode, :airsBeforeSeason, :number, :absoluteNumber, :runtime, :aired, :year, :image, :imageType, :overview, :isMovie) ON CONFLICT (tvdb_id) DO UPDATE SET name = EXCLUDED.name, series_tvdb_id = EXCLUDED.series_tvdb_id, season_number = EXCLUDED.season_number, season_name = EXCLUDED.season_name, airs_after_season = EXCLUDED.airs_after_season, airs_before_episode = EXCLUDED.airs_before_episode, airs_before_season = EXCLUDED.airs_before_season, number = EXCLUDED.number, absolute_number = EXCLUDED.absolute_number, runtime = EXCLUDED.runtime, aired = EXCLUDED.aired, year = EXCLUDED.year, image = EXCLUDED.image, image_type = EXCLUDED.image_type, overview = EXCLUDED.overview")
            .beanMapped()
            .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Long> seriesDeleter() {
        return new JdbcBatchItemWriterBuilder<Long>()
            .dataSource(dataSource)
            .sql("DELETE FROM series WHERE tvdb_id = ?")
            .itemPreparedStatementSetter((id, ps) -> {
                ps.setLong(1, id);
            })
            .assertUpdates(false)
            .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Long> episodeDeleter() {
        return new JdbcBatchItemWriterBuilder<Long>()
            .dataSource(dataSource)
            .sql("DELETE FROM episode WHERE tvdb_id = ?")
            .itemPreparedStatementSetter((id, ps) -> {
                ps.setLong(1, id);
            })
            .assertUpdates(false)
            .build();
    }

    @Bean
    @StepScope
    UpdateRecordWriter updateRecordWriter(
        JdbcBatchItemWriter<Series> seriesWriter,
        JdbcBatchItemWriter<Episode> episodeWriter,
        JdbcBatchItemWriter<Long> seriesDeleter,
        JdbcBatchItemWriter<Long> episodeDeleter
    ) {
        return new UpdateRecordWriter(
            seriesWriter,
            episodeWriter,
            seriesDeleter,
            episodeDeleter
        );
    }

    @Bean
    TimestampJobExecutionListener timestampJobExecutionListener() {
        return new TimestampJobExecutionListener(redisTemplate);
    }

    @Bean
    UpdateJobExecutionListener updateJobExecutionListener(UpdateRecordRepository updateRecordRepository) {
        return new UpdateJobExecutionListener(updateRecordRepository);
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
        UpdateJobExecutionListener updateJobExecutionListener,
        Step cacheUpdateStep,
        Step updateStep
    ) {
        return new JobBuilder("tvdbUpdateSyncJob", jobRepository)
            .listener(updateJobExecutionListener)
            .start(cacheUpdateStep)
            .next(updateStep)
            .build();
    }

    @Bean
    Step seriesStep(JobRepository jobRepository,
        SeriesPagingItemReader seriesReader,
        SeriesProcessor seriesProcessor,
        JdbcBatchItemWriter<Series> seriesWriter
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
        JdbcBatchItemWriter<Episode> episodeWriter
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
    Step cacheUpdateStep(JobRepository jobRepository,
        UpdatePagingItemReader updateReader,
        UpdateProcessor updateProcessor,
        RepositoryItemWriter<UpdateRecord> updateWriter
    ) {
        return new StepBuilder("cacheUpdateStep", jobRepository)
            .<UpdateDto, UpdateRecord>chunk(500)
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
    Step updateStep(JobRepository jobRepository,
        RepositoryItemReader<UpdateRecord> updateRecordReader,
        UpdateRecordProcessor updateRecordProcessor,
        UpdateRecordWriter updateRecordWriter
    ) {
        return new StepBuilder("updateStep", jobRepository)
            .<UpdateRecord, UpdateRecordDto>chunk(500)
            .transactionManager(transactionManager())
            .reader(updateRecordReader)
            .processor(updateRecordProcessor)
            .writer(updateRecordWriter)
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
