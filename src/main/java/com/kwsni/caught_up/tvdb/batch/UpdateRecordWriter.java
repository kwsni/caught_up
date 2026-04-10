package com.kwsni.caught_up.tvdb.batch;

import javax.sql.DataSource;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;

import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.EpisodeRepository;
import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

import jakarta.persistence.EntityManager;

public class UpdateRecordWriter implements ItemWriter<UpdateRecordDto> {
    private JdbcBatchItemWriter<Series> seriesWriter;
    private JdbcBatchItemWriter<Episode> episodeWriter;
    private JdbcBatchItemWriter<Long> seriesDeleter;
    private JdbcBatchItemWriter<Long> episodeDeleter;

    public UpdateRecordWriter(
        JdbcBatchItemWriter<Series> seriesWriter,
        JdbcBatchItemWriter<Episode> episodeWriter,
        JdbcBatchItemWriter<Long> seriesDeleter,
        JdbcBatchItemWriter<Long> episodeDeleter
    ) {
        this.seriesWriter = seriesWriter;
        this.episodeWriter = episodeWriter;
        this.seriesDeleter = seriesDeleter;
        this.episodeDeleter = episodeDeleter;
    }
    
    @Override
    public void write(Chunk<? extends UpdateRecordDto> items) throws Exception {
        Chunk<Series> series = new Chunk<Series>();
        Chunk<Episode> episodes = new Chunk<Episode>();
        Chunk<Long> seriesIdsToDelete = new Chunk<Long>();
        Chunk<Long> episodeIdsToDelete = new Chunk<Long>();

        for(UpdateRecordDto item : items) {
            item.series().ifPresent(series::add);
            item.episode().ifPresent(episodes::add);
            item.tvdbIdToDelete().ifPresent(seriesIdsToDelete::add);
            item.tvdbIdToDelete().ifPresent(episodeIdsToDelete::add);
        }
        
        seriesWriter.write(series);
        episodeWriter.write(episodes);
        seriesDeleter.write(seriesIdsToDelete);
        episodeDeleter.write(episodeIdsToDelete);
    }
}
