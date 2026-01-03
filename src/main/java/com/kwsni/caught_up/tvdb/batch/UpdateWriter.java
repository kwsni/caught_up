package com.kwsni.caught_up.tvdb.batch;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;

import com.kwsni.caught_up.tvdb.dto.UpdateRecord;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.EpisodeRepository;
import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

import jakarta.persistence.EntityManager;

public class UpdateWriter implements ItemWriter<UpdateRecord> {
    RepositoryItemWriter<Series> seriesWriter;
    RepositoryItemWriter<Episode> episodeWriter;
    RepositoryItemWriter<Series> seriesDeleter;
    RepositoryItemWriter<Episode> episodeDeleter;
    EntityManager entityManager;

    public UpdateWriter(
        RepositoryItemWriter<Series> seriesWriter,
        RepositoryItemWriter<Episode> episodeWriter,
        SeriesRepository seriesRepository,
        EpisodeRepository episodeRepository,
        EntityManager entityManager
    ) {
        this.seriesWriter = seriesWriter;
        this.episodeWriter = episodeWriter;
        this.seriesDeleter = new RepositoryItemWriterBuilder<Series>()
            .repository(seriesRepository)
            .methodName("deleteAllInBatch")
            .build();
        this.episodeDeleter = new RepositoryItemWriterBuilder<Episode>()
            .repository(episodeRepository)
            .methodName("deleteAllInBatch")
            .build();
        this.entityManager = entityManager;
    }
    
    @Override
    public void write(Chunk<? extends UpdateRecord> items) throws Exception {
        Chunk<Series> series = new Chunk<Series>();
        Chunk<Episode> episodes = new Chunk<Episode>();
        Chunk<Long> seriesIdsToDelete = new Chunk<Long>();
        Chunk<Long> episodeIdsToDelete = new Chunk<Long>();

        for(UpdateRecord item : items) {
            item.series().ifPresent(series::add);
            item.episode().ifPresent(episodes::add);
            item.tvdbIdToDelete().filter(id -> item.series().isPresent()).ifPresent(seriesIdsToDelete::add);
            item.tvdbIdToDelete().filter(id -> item.episode().isPresent()).ifPresent(episodeIdsToDelete::add);
        }
        
        Chunk<Series> seriesToDelete = new Chunk<Series>(seriesIdsToDelete
            .getItems()
            .stream()
            .map(id -> {
                return entityManager.getReference(Series.class, id);
            })
            .toList()
        );

        Chunk<Episode> episodesToDelete = new Chunk<Episode>(episodeIdsToDelete
            .getItems()
            .stream()
            .map(id -> {
                return entityManager.getReference(Episode.class, id);
            })
            .toList()
        );

        seriesWriter.write(series);
        episodeWriter.write(episodes);
        seriesDeleter.write(seriesToDelete);
        episodeDeleter.write(episodesToDelete);
    }
}
