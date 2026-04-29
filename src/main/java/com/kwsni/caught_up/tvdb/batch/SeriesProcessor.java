package com.kwsni.caught_up.tvdb.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.model.Series;

public class SeriesProcessor implements ItemProcessor<SeriesBaseRecordDto, Series> {
    private TvdbService tvdbSvc;
    private Log logger = LogFactory.getLog(getClass());

    public SeriesProcessor(TvdbService tvdbSvc) {
        this.tvdbSvc = tvdbSvc;
    }

    @Override
    public Series process(SeriesBaseRecordDto seriesDto) throws Exception {
        if(logger.isDebugEnabled()) {
            logger.debug("Processing series with id " + seriesDto.id() + " and name " + seriesDto.name());
        }
        boolean hasNameTrans = seriesDto.nameTranslations().contains("eng");
        boolean hasOverviewTrans = seriesDto.overviewTranslations().contains("eng");
        boolean hasTrans = !seriesDto.originalLanguage().equals("eng") && (hasNameTrans || hasOverviewTrans);

        if(hasTrans) {
            if(logger.isTraceEnabled()) {
                logger.trace("Fetching series trans for " + seriesDto.id() + " with og lang: " + seriesDto.originalLanguage() + hasTrans + " hasNameTrans: " + seriesDto.nameTranslations() + hasNameTrans + " hasOverviewTrans: " +  seriesDto.overviewTranslations() + hasOverviewTrans);
            }
            TransResponseDto.Data translation = tvdbSvc.fetchTrans("series", seriesDto.id()).data();
            return tvdbSvc.mapToSeries(seriesDto, translation);
        } else {
            return tvdbSvc.mapToSeries(seriesDto);
        }
    }
}
