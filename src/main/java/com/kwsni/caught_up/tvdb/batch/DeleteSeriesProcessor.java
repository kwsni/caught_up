package com.kwsni.caught_up.tvdb.batch;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.model.Series;

public class DeleteSeriesProcessor implements ItemProcessor<UpdateRecord, UpdateRecordDto> {
    private TvdbService tvdbSvc;
    private Log logger = LogFactory.getLog(getClass());

    public DeleteSeriesProcessor(TvdbService tvdbSvc) {
        this.tvdbSvc = tvdbSvc;
    }

    @Override
    public UpdateRecordDto process(UpdateRecord update) throws Exception {
        Long recordId = update.getRecordId();
        int methodInt = update.getMethodInt();
        Long mergeToId = update.getMergeToId();
        String mergeToType = update.getMergeToType();

        if(logger.isDebugEnabled()) {
            logger.debug("Processing update for series with id " + recordId + " and method " + methodInt);
        }

        Optional<Series> series = Optional.empty();
        
        if(mergeToId != null && mergeToType.equals("series")) {
            try {
                SeriesBaseRecordDto seriesDto = tvdbSvc.fetchSeries(mergeToId).data();

                boolean hasNameTrans = seriesDto.nameTranslations().contains("eng");
                boolean hasOverviewTrans = seriesDto.overviewTranslations().contains("eng");
                boolean hasTrans = !seriesDto.originalLanguage().equals("eng") && (hasNameTrans || hasOverviewTrans);

                if(hasTrans) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("Fetching series trans for " + mergeToId + " with og lang: " + seriesDto.originalLanguage() + hasTrans + " hasNameTrans: " + seriesDto.nameTranslations() + hasNameTrans + " hasOverviewTrans: " +  seriesDto.overviewTranslations() + hasOverviewTrans);
                    }
                    TransResponseDto.Data translation = tvdbSvc.fetchTrans(mergeToType, mergeToId).data();
                    series = Optional.of(tvdbSvc.mapToSeries(seriesDto, translation));
                } else {
                    series = Optional.of(tvdbSvc.mapToSeries(seriesDto));
                }
            } catch(Exception e) {
                logger.error("Error fetching series data for merge, skipping record.", e);
                throw new InvalidRecordException("Error fetching series data for merge, skipping record.");
            }
        }

        return new UpdateRecordDto(
            series,
            Optional.empty(),
            Optional.of(recordId)
        );
    }
}