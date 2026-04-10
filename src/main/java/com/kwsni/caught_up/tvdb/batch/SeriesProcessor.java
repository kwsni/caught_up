package com.kwsni.caught_up.tvdb.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.model.Series;

public class SeriesProcessor implements ItemProcessor<SeriesBaseRecordDto, Series> {
    private String apiPath;
    private TransResponseDto seriesTransDto;
    private RestClient tvdbClient;
    private Log logger = LogFactory.getLog(getClass());

    public SeriesProcessor(RestClient tvdbClient) {
        this.tvdbClient = tvdbClient;
    }

    @Override
    public Series process(SeriesBaseRecordDto seriesDto) throws Exception {
        Series series;
        if(!seriesDto.originalLanguage().equals("eng") &&
            (seriesDto.nameTranslations().contains("eng") ||
            seriesDto.overviewTranslations().contains("eng"))
        ) {
            apiPath = "/series/" + seriesDto.id() + "/translations/eng";
            seriesTransDto = fetchResponse();

            if(logger.isDebugEnabled()) {
                logger.debug("Processing translated series: " + seriesDto.id() + " - " + seriesTransDto.data().name());
            }

            series = new Series(seriesDto.id(),
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
        } else {
            if(logger.isDebugEnabled()) {
                logger.debug("Processing series: " + seriesDto.id() + " - " + seriesDto.name());
            }
            series = new Series(seriesDto.id(),
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
                seriesDto.slug());
        }
        return series;
    }

    @Retryable(
        maxRetries = 4,
        delay = 1000,
        jitter = 10,
        multiplier = 2,
        maxDelay = 5000
    )
    private TransResponseDto fetchResponse() {
        return tvdbClient.get()
            .uri(apiPath)
            .retrieve()
            .body(TransResponseDto.class);
    }
}
