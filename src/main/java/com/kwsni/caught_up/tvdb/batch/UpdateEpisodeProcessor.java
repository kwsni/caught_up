package com.kwsni.caught_up.tvdb.batch;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

public class UpdateEpisodeProcessor implements ItemProcessor<UpdateRecord, UpdateRecordDto> {
    private TvdbService tvdbSvc;
    private Log logger = LogFactory.getLog(getClass());

    public UpdateEpisodeProcessor(TvdbService tvdbSvc) {
        this.tvdbSvc = tvdbSvc;
    }

    @Override
    public UpdateRecordDto process(UpdateRecord update) throws Exception {
        String entityType = update.getEntityType();
        Long recordId = update.getRecordId();
        int methodInt = update.getMethodInt();

        if(logger.isDebugEnabled()) {
            logger.debug("Processing update record for episode with id " + recordId + " and method " + methodInt);
        }

        Optional<Episode> episode = Optional.empty();
        Optional<Series> series = Optional.empty();
        
        EpisodeBaseRecordDto episodeDto = tvdbSvc.fetchEpisode(recordId).data();
        SeriesBaseRecordDto seriesDto = tvdbSvc.fetchSeries(episodeDto.seriesId()).data();

        boolean hasNameTrans = seriesDto.nameTranslations().contains("eng");
        boolean hasOverviewTrans = seriesDto.overviewTranslations().contains("eng");
        boolean hasTrans = !seriesDto.originalLanguage().equals("eng") && (hasNameTrans || hasOverviewTrans);

        if(hasTrans) {
            if(logger.isTraceEnabled()) {
                logger.trace("Fetching series trans for " + seriesDto.id() + " with og lang: " + seriesDto.originalLanguage() + hasTrans + " hasNameTrans: " + seriesDto.nameTranslations() + hasNameTrans + " hasOverviewTrans: " +  seriesDto.overviewTranslations() + hasOverviewTrans);
            }
            TransResponseDto.Data translation = tvdbSvc.fetchTrans("series", seriesDto.id()).data();
            series = Optional.of(tvdbSvc.mapToSeries(seriesDto, translation));
        } else {
            series = Optional.of(tvdbSvc.mapToSeries(seriesDto));
        }

        hasNameTrans = episodeDto.nameTranslations().contains("eng");
        hasOverviewTrans = episodeDto.overviewTranslations().contains("eng");
        hasTrans = hasNameTrans || hasOverviewTrans;

        if(hasTrans) {
            if(logger.isTraceEnabled()) {
                logger.trace("Fetching episode trans for " + recordId + " hasNameTrans: " + episodeDto.nameTranslations() + hasNameTrans + " hasOverviewTrans: " +  episodeDto.overviewTranslations() + hasOverviewTrans);
            }
            TransResponseDto.Data translation = tvdbSvc.fetchTrans(entityType, recordId).data();
            episode = Optional.of(tvdbSvc.mapToEpisode(episodeDto, translation));
        } else {
            episode = Optional.of(tvdbSvc.mapToEpisode(episodeDto));
        }

        return new UpdateRecordDto(
            series,
            episode,
            Optional.empty()
        );
    }
}
