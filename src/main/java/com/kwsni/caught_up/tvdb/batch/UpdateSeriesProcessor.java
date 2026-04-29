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

public class UpdateSeriesProcessor implements ItemProcessor<UpdateRecord, UpdateRecordDto> {
    private TvdbService tvdbSvc;
    private Log logger = LogFactory.getLog(getClass());

    public UpdateSeriesProcessor(TvdbService tvdbSvc) {
        this.tvdbSvc = tvdbSvc;
    }

    @Override
    public UpdateRecordDto process(UpdateRecord update) throws Exception {
        String entityType = update.getEntityType();
        Long recordId = update.getRecordId();
        int methodInt = update.getMethodInt();

        if(logger.isDebugEnabled()) {
            logger.debug("Processing update record for series with id " + recordId + " and method " + methodInt);
        }

        Optional<Series> series = Optional.empty();

        SeriesBaseRecordDto seriesDto = tvdbSvc.fetchSeries(recordId).data();

        boolean hasNameTrans = seriesDto.nameTranslations().contains("eng");
        boolean hasOverviewTrans = seriesDto.overviewTranslations().contains("eng");
        boolean hasTrans = !seriesDto.originalLanguage().equals("eng") && (hasNameTrans || hasOverviewTrans);

        if(hasTrans) {
            if(logger.isTraceEnabled()) {
                logger.trace("Fetching series trans for " + recordId + " with og lang: " + seriesDto.originalLanguage() + hasTrans + " hasNameTrans: " + seriesDto.nameTranslations() + hasNameTrans + " hasOverviewTrans: " +  seriesDto.overviewTranslations() + hasOverviewTrans);
            }
            TransResponseDto.Data translation = tvdbSvc.fetchTrans(entityType, recordId).data();
            series = Optional.of(tvdbSvc.mapToSeries(seriesDto, translation));
        } else {
            series = Optional.of(tvdbSvc.mapToSeries(seriesDto));
        }
        
        return new UpdateRecordDto(
            series,
            Optional.empty(),
            Optional.empty()
        );
    }
}