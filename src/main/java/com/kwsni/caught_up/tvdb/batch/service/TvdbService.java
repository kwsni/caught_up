package com.kwsni.caught_up.tvdb.batch.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.EpisodeListResponseDto;
import com.kwsni.caught_up.tvdb.dto.EpisodeResponseDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesEpisodeListResponseDto;
import com.kwsni.caught_up.tvdb.dto.SeriesListResponseDto;
import com.kwsni.caught_up.tvdb.dto.SeriesResponseDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

@Service
public class TvdbService {
    private final RestClient tvdbClient;
    private final SeriesRepository seriesRepo;

    public TvdbService(RestClient tvdbClient, SeriesRepository seriesRepo) {
        this.tvdbClient = tvdbClient;
        this.seriesRepo = seriesRepo;
    }

    public SeriesListResponseDto fetchSeriesList(int page) {
        return tvdbClient.get()
            .uri("/series?page={page}", page)
            .retrieve()
            .body(SeriesListResponseDto.class);
    }

    public SeriesResponseDto fetchSeries(Long seriesId) {
        return tvdbClient.get()
            .uri("/series/{seriesId}", seriesId)
            .retrieve()
            .body(SeriesResponseDto.class);
    }

    public EpisodeListResponseDto fetchEpisodeList(int page) {
        return tvdbClient.get()
            .uri("/episodes?page={page}", page)
            .retrieve()
            .body(EpisodeListResponseDto.class);
    }

    public SeriesEpisodeListResponseDto fetchSeriesEpisodeList(Long seriesId, int page) {
        return tvdbClient.get()
            .uri("/series/{seriesId}/episodes/default/eng?page={page}", seriesId, page)
            .retrieve()
            .body(SeriesEpisodeListResponseDto.class);
    }

    public EpisodeResponseDto fetchEpisode(Long episodeId) {
        return tvdbClient.get()
            .uri("/episodes/{episodeId}", episodeId)
            .retrieve()
            .body(EpisodeResponseDto.class);
    }

    public UpdateResponseDto fetchUpdates(String lastUpdated, int page) {
        return tvdbClient.get()
            .uri("/updates?since={lastUpdated}&page={page}", lastUpdated, page)
            .retrieve()
            .body(UpdateResponseDto.class);
    }

    public TransResponseDto fetchTrans(String entityType, Long entityId) {
        return tvdbClient.get()
            .uri("/{entityType}/{entityId}/translations/eng", entityType, entityId)
            .retrieve()
            .body(TransResponseDto.class);
    }

    public Series mapToSeries(SeriesBaseRecordDto seriesDto) {
        return new Series(
            seriesDto.id(),
            seriesDto.name(),
            seriesDto.year(),
            seriesDto.firstAired(),
            seriesDto.lastAired(),
            seriesDto.nextAired(),
            seriesDto.score(),
            seriesDto.image(),
            seriesDto.overview(),
            seriesDto.country(),
            seriesDto.lastUpdated(),
            seriesDto.slug()
        );
    }
    
    public Series mapToSeries(SeriesBaseRecordDto seriesDto, TransResponseDto.Data translation) {
        return new Series(
            seriesDto.id(),
            translation.name(),
            seriesDto.year(),
            seriesDto.firstAired(),
            seriesDto.lastAired(),
            seriesDto.nextAired(),
            seriesDto.score(),
            seriesDto.image(),
            translation.overview(),
            seriesDto.country(),
            seriesDto.lastUpdated(),
            seriesDto.slug()
        );
    }

    public Episode mapToEpisode(EpisodeBaseRecordDto episodeDto) {
        return new Episode(
            episodeDto.id(),
            episodeDto.name(),
            seriesRepo.getReferenceById(episodeDto.seriesId()),
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
    }

    public Episode mapToEpisode(EpisodeBaseRecordDto episodeDto, TransResponseDto.Data translation) {
        return new Episode(
            episodeDto.id(),
            translation.name(),
            seriesRepo.getReferenceById(episodeDto.seriesId()),
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
            translation.overview(),
            episodeDto.isMovie()
        );
    }
}
