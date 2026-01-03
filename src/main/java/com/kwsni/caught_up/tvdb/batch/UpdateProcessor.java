package com.kwsni.caught_up.tvdb.batch;

import java.util.Optional;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.EpisodeResponseDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesResponseDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecord;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.Update;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

import jakarta.persistence.EntityManager;

public class UpdateProcessor implements ItemProcessor<Update, UpdateRecord> {
    private String apiPath;
    private String transApiPath;
    private EntityManager entityManager;
    private RestClient tvdbClient;

    public UpdateProcessor(EntityManager entityManager, RestClient tvdbClient) {
        this.entityManager = entityManager;
        this.tvdbClient = tvdbClient;
    }

    @Override
    public UpdateRecord process(Update update) throws Exception {
        String entityType = update.entityType();
        Long recordId = update.recordId();
        int methodInt = update.methodInt();
        Long mergeToId = update.mergeToId();

        apiPath = String.format("/%s/%d", entityType, recordId);
        transApiPath = String.format("%s/translations/eng", apiPath);

        if(entityType == "series") {
            if(methodInt == 3) {
                Optional<Series> series = Optional.empty(); 
                if(update.mergeToId() != null) {
                    apiPath = String.format("/%s/%d", entityType, mergeToId);
                    transApiPath = String.format("%s/translations/eng", apiPath);

                    SeriesBaseRecordDto seriesDto = fetchSeriesResponse().data();
                    TransResponseDto seriesTransDto = fetchTransResponse();

                    series = Optional.of(new Series(
                        seriesDto.id(),
                        seriesTransDto.data().name(),
                        seriesDto.year(),
                        seriesDto.firstAired(),
                        seriesDto.lastAired(),
                        seriesDto.nextAired(),
                        seriesDto.score(),
                        seriesDto.image(),
                        seriesTransDto.data().overview(),
                        seriesDto.country(),
                        seriesDto.lastUpdated(),
                        seriesDto.slug()
                    ));
                }

                return new UpdateRecord(
                    series,
                    Optional.empty(),
                    Optional.of(update.seriesId())
                );
            } else {
                SeriesBaseRecordDto seriesDto = fetchSeriesResponse().data();
                TransResponseDto seriesTransDto = fetchTransResponse();

                Series series = new Series(
                    seriesDto.id(),
                    seriesTransDto.data().name(),
                    seriesDto.year(),
                    seriesDto.firstAired(),
                    seriesDto.lastAired(),
                    seriesDto.nextAired(),
                    seriesDto.score(),
                    seriesDto.image(),
                    seriesTransDto.data().overview(),
                    seriesDto.country(),
                    seriesDto.lastUpdated(),
                    seriesDto.slug());
            
                return new UpdateRecord(
                    Optional.of(series),
                    Optional.empty(),
                    Optional.empty()
                );
            }
        } else if(entityType == "episodes") {
            if(methodInt == 3) {
                Optional<Episode> episode = Optional.empty();
                if(update.mergeToId() != null) {
                    apiPath = String.format("/%s/%d", entityType, mergeToId);
                    transApiPath = String.format("%s/translations/eng", apiPath);

                    EpisodeBaseRecordDto episodeDto = fetchEpisodeResponse().data();
                    TransResponseDto episodeTransDto = fetchTransResponse();

                    episode = Optional.of(new Episode(
                        episodeDto.id(),
                        episodeTransDto.data().name(),
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
                        episodeTransDto.data().overview(),
                        episodeDto.isMovie()
                    ));
                }
                return new UpdateRecord(
                    Optional.empty(),
                    episode,
                    Optional.of(update.recordId())
                );
            } else {
                EpisodeBaseRecordDto episodeDto = fetchEpisodeResponse().data();
                TransResponseDto episodeTransDto = fetchTransResponse();

                Episode episode = new Episode(
                    episodeDto.id(),
                    episodeTransDto.data().name(),
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
                    episodeTransDto.data().overview(),
                    episodeDto.isMovie());

                return new UpdateRecord(
                    Optional.empty(),
                    Optional.of(episode),
                    Optional.empty()
                );
            }
        } else {
            throw new InvalidRecordException("Not a series or episode, skipping...");
        }        
    }

    private SeriesResponseDto fetchSeriesResponse() {
        return tvdbClient.get()
            .uri(apiPath)
            .retrieve()
            .body(SeriesResponseDto.class);
    }

    private EpisodeResponseDto fetchEpisodeResponse() {
        return tvdbClient.get()
            .uri(apiPath)
            .retrieve()
            .body(EpisodeResponseDto.class);
    }

    private TransResponseDto fetchTransResponse() {
        return tvdbClient.get()
            .uri(transApiPath)
            .retrieve()
            .body(TransResponseDto.class);
    }
}
