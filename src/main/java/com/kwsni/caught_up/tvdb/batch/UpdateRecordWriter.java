package com.kwsni.caught_up.tvdb.batch;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;

import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

public class UpdateRecordWriter implements ItemWriter<UpdateRecordDto> {
    private JdbcBatchItemWriter<Series> seriesWriter;
    private JdbcBatchItemWriter<Episode> episodeWriter;
    private JdbcBatchItemWriter<Long> seriesDeleter;
    private JdbcBatchItemWriter<Long> episodeDeleter;
    private JdbcBatchItemWriter<Long> updateRecordDeleter;

    public UpdateRecordWriter(
        JdbcBatchItemWriter<Series> seriesWriter,
        JdbcBatchItemWriter<Episode> episodeWriter,
        JdbcBatchItemWriter<Long> seriesDeleter,
        JdbcBatchItemWriter<Long> episodeDeleter,
        JdbcBatchItemWriter<Long> updateRecordDeleter
    ) {
        this.seriesWriter = seriesWriter;
        this.episodeWriter = episodeWriter;
        this.seriesDeleter = seriesDeleter;
        this.episodeDeleter = episodeDeleter;
        this.updateRecordDeleter = updateRecordDeleter;
    }
    
    @Override
    public void write(Chunk<? extends UpdateRecordDto> items) throws Exception {
        Chunk<Series> series = new Chunk<Series>();
        Chunk<Episode> episodes = new Chunk<Episode>();
        Chunk<Long> seriesRecordIds = new Chunk<Long>();
        Chunk<Long> episodeRecordIds = new Chunk<Long>();
        Chunk<Long> seriesIdsToDelete = new Chunk<Long>();
        Chunk<Long> episodeIdsToDelete = new Chunk<Long>();

        for(UpdateRecordDto item : items) {
            item.series().ifPresent(series::add);
            if(item.series().isPresent()) {
                seriesRecordIds.add(item.series().get().getTvdbId());
            }
            item.episode().ifPresent(episodes::add);
            if(item.episode().isPresent()) {
                episodeRecordIds.add(item.episode().get().getTvdbId());
            }
            item.tvdbIdToDelete().ifPresent(seriesIdsToDelete::add);
            item.tvdbIdToDelete().ifPresent(episodeIdsToDelete::add);
        }
        
        seriesWriter.write(series);
        episodeWriter.write(episodes);
        episodeDeleter.write(episodeIdsToDelete);
        updateRecordDeleter.write(episodeIdsToDelete);
        updateRecordDeleter.write(episodeRecordIds);
        seriesDeleter.write(seriesIdsToDelete);
        updateRecordDeleter.write(seriesIdsToDelete);
        updateRecordDeleter.write(seriesRecordIds);
    }
}
