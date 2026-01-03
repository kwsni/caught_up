package com.kwsni.caught_up.tvdb.batch;

import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

import jakarta.persistence.EntityManager;

public class EpisodeProcessor implements ItemProcessor<EpisodeBaseRecordDto, Episode> {
    private EntityManager entityManager;

    public EpisodeProcessor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Episode process(EpisodeBaseRecordDto episodeDto) throws Exception {
        Episode episode = new Episode(episodeDto.id(),
            episodeDto.name(),
            entityManager.getReference(Series.class,
                episodeDto.seriesId()
            ),
            episodeDto.seasonNumber(),
            episodeDto.seasonName(),
            episodeDto.airsAfterSeason(),
            episodeDto.airsBeforeEpisode(),
            episodeDto.airsBeforeSeason(),
            episodeDto.number(),
            episodeDto.absoluteNumber(),
            episodeDto.runtime(),
            episodeDto.aired(),
            episodeDto.year(),
            episodeDto.image(),
            episodeDto.imageType(),
            episodeDto.overview(),
            episodeDto.isMovie()
        );
        return episode;
    }
}
