package com.kwsni.caught_up.config;

import java.util.Map;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.infrastructure.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.kwsni.caught_up.tvdb.batch.DeleteEpisodeProcessor;
import com.kwsni.caught_up.tvdb.batch.DeleteSeriesProcessor;
import com.kwsni.caught_up.tvdb.batch.EpisodeProcessor;
import com.kwsni.caught_up.tvdb.batch.InitialSyncJobExecutionListener;
import com.kwsni.caught_up.tvdb.batch.InvalidRecordException;
import com.kwsni.caught_up.tvdb.batch.SeriesEpisodeItemReader;
import com.kwsni.caught_up.tvdb.batch.SeriesPagingItemReader;
import com.kwsni.caught_up.tvdb.batch.SeriesProcessor;
import com.kwsni.caught_up.tvdb.batch.UpdateEpisodeProcessor;
import com.kwsni.caught_up.tvdb.batch.UpdateJobExecutionListener;
import com.kwsni.caught_up.tvdb.batch.UpdatePagingItemReader;
import com.kwsni.caught_up.tvdb.batch.UpdateProcessor;
import com.kwsni.caught_up.tvdb.batch.UpdateRecordWriter;
import com.kwsni.caught_up.tvdb.batch.UpdateSeriesProcessor;
import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.batch.repository.UpdateRecordRepository;
import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.UpdateDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

@Configuration
public class TvdbBatchJobConfig {

    @Bean
    Job tvdbInitialSyncJob(
        JobRepository jobRepository,
        InitialSyncJobExecutionListener initialSyncJobExecutionListener,
        Step seriesStep,
        Step episodeStep
    ) {
        return new JobBuilder("initial-sync-job", jobRepository)
            .listener(initialSyncJobExecutionListener)
            .start(seriesStep)
            .next(episodeStep)
            .build();
    }

    @Bean
    Job tvdbUpdateSyncJob(
        JobRepository jobRepository,
        UpdateJobExecutionListener updateJobExecutionListener,
        Step cacheUpdateStep,
        Step updateStep
    ) {
        return new JobBuilder("update-sync-job", jobRepository)
            .listener(updateJobExecutionListener)
            .start(cacheUpdateStep)
            .next(updateStep)
            .build();
    }

    @Bean
    Step seriesStep(
        JobRepository jobRepository,
        SeriesPagingItemReader seriesReader,
        SeriesProcessor seriesProcessor,
        JdbcBatchItemWriter<Series> seriesWriter
    ) {
        return new StepBuilder("initial-series-step", jobRepository)
            .<SeriesBaseRecordDto, Series>chunk(50)
            .reader(seriesReader)
            .processor(seriesProcessor)
            .writer(seriesWriter)
            .build();
    }

    @Bean
    Step episodeStep(
        JobRepository jobRepository,
        SeriesEpisodeItemReader seriesEpisodeReader,
        EpisodeProcessor episodeProcessor,
        JdbcBatchItemWriter<Episode> episodeWriter
    ) {
        return new StepBuilder("initial-episode-step", jobRepository)
            .<EpisodeBaseRecordDto, Episode>chunk(50)
            .reader(seriesEpisodeReader)
            .processor(episodeProcessor)
            .writer(episodeWriter)
            .build();
    }

    @Bean
    Step cacheUpdateStep(
        JobRepository jobRepository,
        UpdatePagingItemReader updateReader,
        UpdateProcessor updateProcessor,
        RepositoryItemWriter<UpdateRecord> updateWriter
    ) {
        return new StepBuilder("cache-update-step", jobRepository)
            .<UpdateDto, UpdateRecord>chunk(10)
            .reader(updateReader)
            .processor(updateProcessor)
            .writer(updateWriter)
            .build();
    }

    @Bean
    Step updateStep(
        JobRepository jobRepository,
        RepositoryItemReader<UpdateRecord> updateRecordReader,
        AsyncItemProcessor<UpdateRecord, UpdateRecordDto> asyncUpdateRecordProcessor,
        AsyncItemWriter<UpdateRecordDto> asyncUpdateRecordWriter
    ) {
        return new StepBuilder("update-step", jobRepository)
            .<UpdateRecord, Future<UpdateRecordDto>>chunk(10)
            .reader(updateRecordReader)
            .processor(asyncUpdateRecordProcessor)
            .writer(asyncUpdateRecordWriter)
            .build();
    }

    @Bean
    @StepScope
    SeriesPagingItemReader seriesReader(TvdbService tvdbSvc) {
        return new SeriesPagingItemReader(tvdbSvc);
    }

    @Bean
    @StepScope
    SeriesEpisodeItemReader seriesEpisodeReader(TvdbService tvdbSvc, JdbcPagingItemReader<Long> dbSeriesReader) {
        return new SeriesEpisodeItemReader(tvdbSvc, dbSeriesReader);
    }

    @Bean
    @StepScope
    UpdatePagingItemReader updateReader(
        TvdbService tvdbSvc,
        RedisTemplate<String, String> redisTemplate
    ) {
        return new UpdatePagingItemReader(tvdbSvc, redisTemplate);
    }

    @Bean
    @StepScope
    JdbcPagingItemReader<Long> dbSeriesReader(DataSource dataSource) throws Exception {
        return new JdbcPagingItemReaderBuilder<Long>()
            .name("dbSeriesReader")
            .dataSource(dataSource)
            .selectClause("tvdb_id")
            .fromClause("series")
            .sortKeys(Map.of("tvdb_id", Order.ASCENDING))
            .rowMapper(new SingleColumnRowMapper<>(Long.class))
            .pageSize(100)
            .build();
    }

    @Bean
    @StepScope
    RepositoryItemReader<UpdateRecord> updateRecordReader(UpdateRecordRepository updateRecordRepository) {
        return new RepositoryItemReaderBuilder<UpdateRecord>()
            .repository(updateRecordRepository)
            .methodName("findAll")
            .pageSize(10)
            .sorts(Map.of("timestamp", Direction.ASC, "entityType", Direction.ASC))
            .saveState(false)
            .build();
    }

    @Bean
    @StepScope
    SeriesProcessor seriesProcessor(TvdbService tvdbSvc) {
        return new SeriesProcessor(tvdbSvc);
    }

    @Bean
    @StepScope
    EpisodeProcessor episodeProcessor(TvdbService tvdbSvc) {
        return new EpisodeProcessor(tvdbSvc);
    }

    @Bean
    @StepScope
    UpdateProcessor updateProcessor() {
        return new UpdateProcessor();
    }

    @Bean
    @StepScope
    DeleteSeriesProcessor deleteSeriesProcessor(TvdbService tvdbSvc) {
        return new DeleteSeriesProcessor(tvdbSvc);
    }

    @Bean
    @StepScope
    UpdateSeriesProcessor updateSeriesProcessor(TvdbService tvdbSvc) {
        return new UpdateSeriesProcessor(tvdbSvc);
    }

    @Bean
    @StepScope
    DeleteEpisodeProcessor deleteEpisodeProcessor(TvdbService tvdbSvc) {
        return new DeleteEpisodeProcessor(tvdbSvc);
    }

    @Bean
    @StepScope
    UpdateEpisodeProcessor updateEpisodeProcessor(TvdbService tvdbSvc) {
        return new UpdateEpisodeProcessor(tvdbSvc);
    }

    @Bean
    @StepScope
    AsyncItemProcessor<SeriesBaseRecordDto, Series> asyncSeriesProcessor(SeriesProcessor seriesProcessor) {
        var asyncSeriesProcessor = new AsyncItemProcessor<SeriesBaseRecordDto, Series>(seriesProcessor);
        asyncSeriesProcessor.setTaskExecutor(new VirtualThreadTaskExecutor("series-"));
        return asyncSeriesProcessor;
    }

    @Bean
    @StepScope
    AsyncItemProcessor<EpisodeBaseRecordDto, Episode> asyncEpisodeProcessor(EpisodeProcessor episodeProcessor) {
        var asyncEpisodeProcessor = new AsyncItemProcessor<EpisodeBaseRecordDto, Episode>(episodeProcessor);
        asyncEpisodeProcessor.setTaskExecutor(new VirtualThreadTaskExecutor("episode-"));
        return asyncEpisodeProcessor;
    }
    
    @Bean
    @StepScope
    ClassifierCompositeItemProcessor<UpdateRecord, UpdateRecordDto> classifierCompositeUpdateProcessor (
        DeleteSeriesProcessor deleteSeriesProcessor,
        UpdateSeriesProcessor updateSeriesProcessor,
        DeleteEpisodeProcessor deleteEpisodeProcessor,
        UpdateEpisodeProcessor updateEpisodeProcessor
    ) {
        ClassifierCompositeItemProcessor<UpdateRecord, UpdateRecordDto> processor = new ClassifierCompositeItemProcessor<>();
        processor.setClassifier(updateRecord -> {
            if(updateRecord.getEntityType().equals("series")) {
                if(updateRecord.getMethodInt() == 3) {
                    return deleteSeriesProcessor;
                } else {
                    return updateSeriesProcessor;
                }
            } else if(updateRecord.getEntityType().equals("episodes")) {
                if(updateRecord.getMethodInt() == 3) {
                    return deleteEpisodeProcessor;
                } else {
                    return updateEpisodeProcessor;
                }
            } else {
                throw new InvalidRecordException("Not a series or episode, skipping...");
            }
        });
        return processor;
    }

    @Bean
    @StepScope
    AsyncItemProcessor<UpdateRecord, UpdateRecordDto> asyncUpdateRecordProcessor(ClassifierCompositeItemProcessor<UpdateRecord, UpdateRecordDto> classifierCompositeUpdateProcessor) {
        var asyncUpdateRecordProcessor = new AsyncItemProcessor<>(classifierCompositeUpdateProcessor);
        asyncUpdateRecordProcessor.setTaskExecutor(new VirtualThreadTaskExecutor("update-"));
        return asyncUpdateRecordProcessor;
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
    AsyncItemWriter<Series> asyncSeriesWriter(JdbcBatchItemWriter<Series> seriesWriter) {
        return new AsyncItemWriter<>(seriesWriter);
    }

    @Bean
    @StepScope
    AsyncItemWriter<Episode> asyncEpisodeWriter(JdbcBatchItemWriter<Episode> episodeWriter) {
        return new AsyncItemWriter<>(episodeWriter);
    }

    @Bean
    @StepScope
    ClassifierCompositeItemWriter<UpdateRecordDto> classifierCompositeUpdateWriter(
        UpdateRecordWriter updateRecordWriter
    ) {
        ClassifierCompositeItemWriter<UpdateRecordDto> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(updateRecordDto -> updateRecordWriter);
        return writer;
    }

    @Bean
    @StepScope
    AsyncItemWriter<UpdateRecordDto> asyncUpdateRecordWriter(UpdateRecordWriter updateRecordWriter) {
        return new AsyncItemWriter<>(updateRecordWriter);
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Series> seriesWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Series>()
            .dataSource(dataSource)
            .sql("INSERT INTO series (tvdb_id, name, year, first_aired, last_aired, next_aired, score, image, overview, country, last_updated, slug) VALUES (:tvdbId, :name, :year, :firstAired, :lastAired, :nextAired, :score, :image, :overview, :country, :lastUpdated, :slug) ON CONFLICT (tvdb_id) DO UPDATE SET name = EXCLUDED.name, year = EXCLUDED.year, first_aired = EXCLUDED.first_aired, last_aired = EXCLUDED.last_aired, next_aired = EXCLUDED.next_aired, score = EXCLUDED.score, image = EXCLUDED.image, overview = EXCLUDED.overview, country = EXCLUDED.country, last_updated = EXCLUDED.last_updated")
            .beanMapped()
            .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Episode> episodeWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Episode>()
            .dataSource(dataSource)
            .sql("INSERT INTO episode (tvdb_id, name, series_tvdb_id, season_number, season_name, airs_after_season, airs_before_episode, airs_before_season, number, absolute_number, runtime, aired, year, image, image_type, overview, is_movie) VALUES (:tvdbId, :name, :series.tvdbId, :seasonNumber, :seasonName, :airsAfterSeason, :airsBeforeEpisode, :airsBeforeSeason, :number, :absoluteNumber, :runtime, :aired, :year, :image, :imageType, :overview, :isMovie) ON CONFLICT (tvdb_id) DO UPDATE SET name = EXCLUDED.name, series_tvdb_id = EXCLUDED.series_tvdb_id, season_number = EXCLUDED.season_number, season_name = EXCLUDED.season_name, airs_after_season = EXCLUDED.airs_after_season, airs_before_episode = EXCLUDED.airs_before_episode, airs_before_season = EXCLUDED.airs_before_season, number = EXCLUDED.number, absolute_number = EXCLUDED.absolute_number, runtime = EXCLUDED.runtime, aired = EXCLUDED.aired, year = EXCLUDED.year, image = EXCLUDED.image, image_type = EXCLUDED.image_type, overview = EXCLUDED.overview")
            .beanMapped()
            .build();
    }

    @Bean
    @StepScope
    JdbcBatchItemWriter<Long> seriesDeleter(DataSource dataSource) {
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
    JdbcBatchItemWriter<Long> episodeDeleter(DataSource dataSource) {
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
    JdbcBatchItemWriter<Long> updateRecordDeleter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Long>()
            .dataSource(dataSource)
            .sql("DELETE FROM update_record WHERE record_id = ?")
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
        JdbcBatchItemWriter<Long> episodeDeleter,
        JdbcBatchItemWriter<Long> updateRecordDeleter
    ) {
        return new UpdateRecordWriter(
            seriesWriter,
            episodeWriter,
            seriesDeleter,
            episodeDeleter,
            updateRecordDeleter
        );
    }

    @Bean
    InitialSyncJobExecutionListener initialSyncJobExecutionListener(RedisTemplate<String, String> redisTemplate) {
        return new InitialSyncJobExecutionListener(redisTemplate);
    }

    @Bean
    UpdateJobExecutionListener updateJobExecutionListener(RedisTemplate<String, String> redisTemplate) {
        return new UpdateJobExecutionListener(redisTemplate);
    }
}
