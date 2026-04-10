package com.kwsni.caught_up.tvdb.batch;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.EpisodeResponseDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesResponseDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.UpdateDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

import jakarta.persistence.EntityManager;

public class UpdateRecordProcessor implements ItemProcessor<UpdateRecord, UpdateRecordDto> {
    private String apiPath;
    private String transApiPath;
    private EntityManager entityManager;
    private RestClient tvdbClient;
    private Log logger = LogFactory.getLog(getClass());

    public UpdateRecordProcessor(EntityManager entityManager, RestClient tvdbClient) {
        this.entityManager = entityManager;
        this.tvdbClient = tvdbClient;
    }

    @Override
    public UpdateRecordDto process(UpdateRecord update) throws Exception {
        String entityType = update.getEntityType();
        Long recordId = update.getRecordId();
        int methodInt = update.getMethodInt();
        Long mergeToId = update.getMergeToId();
        String mergeToType = update.getMergeToType();

        if(logger.isDebugEnabled()) {
            logger.debug("Processing update for " + entityType + " with id " + recordId + " and method " + methodInt);
        }

        if(entityType.equals("series")) {
            Optional<Series> series = Optional.empty();
            if(methodInt == 3) {
                if(mergeToId != null && mergeToType.equals("series")) {
                    apiPath = "/" + entityType + "/" + mergeToId;
                    transApiPath = apiPath + "/translations/eng";

                    SeriesBaseRecordDto seriesDto = fetchSeriesResponse().data();

                    boolean hasTrans = !seriesDto.originalLanguage().equals("eng") &&
                        (seriesDto.nameTranslations().contains("eng") ||
                        seriesDto.overviewTranslations().contains("eng"));

                    series = Optional.of(new Series(
                        seriesDto.id(),
                        hasTrans ? fetchTransResponse().data().name() : seriesDto.name(),
                        seriesDto.year(),
                        seriesDto.firstAired(),
                        seriesDto.lastAired(),
                        seriesDto.nextAired(),
                        seriesDto.score(),
                        seriesDto.image(),
                        hasTrans ? fetchTransResponse().data().overview() : seriesDto.overview(),
                        seriesDto.country(),
                        seriesDto.lastUpdated(),
                        seriesDto.slug()
                    ));
                }

                if(logger.isTraceEnabled()) {
                    logger.trace("Series to delete: " + series);
                }

                return new UpdateRecordDto(
                    series,
                    Optional.empty(),
                    Optional.of(recordId)
                );
            } else {
                apiPath = "/" + entityType + "/" + recordId;
                transApiPath = apiPath + "/translations/eng";

                SeriesBaseRecordDto seriesDto = fetchSeriesResponse().data();

                boolean hasTrans = !seriesDto.originalLanguage().equals("eng") &&
                    (seriesDto.nameTranslations().contains("eng") ||
                    seriesDto.overviewTranslations().contains("eng"));

                series = Optional.of(new Series(
                    seriesDto.id(),
                    hasTrans ? fetchTransResponse().data().name() : seriesDto.name(),
                    seriesDto.year(),
                    seriesDto.firstAired(),
                    seriesDto.lastAired(),
                    seriesDto.nextAired(),
                    seriesDto.score(),
                    seriesDto.image(),
                    hasTrans ? fetchTransResponse().data().overview() : seriesDto.overview(),
                    seriesDto.country(),
                    seriesDto.lastUpdated(),
                    seriesDto.slug()
                ));

                if(logger.isTraceEnabled()) {
                    logger.trace("Series to update: " + series);
                }
            
                return new UpdateRecordDto(
                    series,
                    Optional.empty(),
                    Optional.empty()
                );
            }
        } else if(entityType.equals("episodes")) {
            Optional<Episode> episode = Optional.empty();
            Optional<Series> series = Optional.empty();
            if(methodInt == 3) {
                if(mergeToId != null && mergeToType.equals("episodes")) {
                    apiPath = "/" + entityType + "/" + mergeToId;
                    transApiPath = apiPath + "/translations/eng";

                    EpisodeBaseRecordDto episodeDto = fetchEpisodeResponse().data();
                    boolean hasTrans = episodeDto.nameTranslations().contains("eng") ||
                        episodeDto.overviewTranslations().contains("eng");

                    episode = Optional.of(new Episode(
                        episodeDto.id(),
                        hasTrans ? fetchTransResponse().data().name() : episodeDto.name(),
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
                        hasTrans ? fetchTransResponse().data().overview() : episodeDto.overview(),
                        episodeDto.isMovie()
                    ));
                }

                if(logger.isTraceEnabled()) {
                    logger.trace("Episode to delete: " + episode);
                }

                return new UpdateRecordDto(
                    Optional.empty(),
                    episode,
                    Optional.of(recordId)
                );
            } else {
                apiPath = "/episodes/" + recordId;
                EpisodeBaseRecordDto episodeDto = fetchEpisodeResponse().data();

                apiPath = "/series/" + episodeDto.seriesId();
                SeriesBaseRecordDto seriesDto = fetchSeriesResponse().data();

                boolean hasTrans = !seriesDto.originalLanguage().equals("eng") &&
                    (seriesDto.nameTranslations().contains("eng") ||
                    seriesDto.overviewTranslations().contains("eng"));
                apiPath = "/series/" + seriesDto.id();
                transApiPath = apiPath + "/translations/eng";

                series = Optional.of(new Series(
                    seriesDto.id(),
                    hasTrans ? fetchTransResponse().data().name() : seriesDto.name(),
                    seriesDto.year(),
                    seriesDto.firstAired(),
                    seriesDto.lastAired(),
                    seriesDto.nextAired(),
                    seriesDto.score(),
                    seriesDto.image(),
                    hasTrans ? fetchTransResponse().data().overview() : seriesDto.overview(),
                    seriesDto.country(),
                    seriesDto.lastUpdated(),
                    seriesDto.slug()
                ));

                hasTrans = episodeDto.nameTranslations().contains("eng") ||
                        episodeDto.overviewTranslations().contains("eng");
                apiPath = "/episodes/" + recordId;
                transApiPath = apiPath + "/translations/eng";

                episode = Optional.of(new Episode(
                    episodeDto.id(),
                    hasTrans ? fetchTransResponse().data().name() : episodeDto.name(),
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
                    hasTrans ? fetchTransResponse().data().overview() : episodeDto.overview(),
                    episodeDto.isMovie()
                ));

                if(logger.isTraceEnabled()) {
                    logger.trace("Episode to update: " + episode);
                }

                return new UpdateRecordDto(
                    series,
                    episode,
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
