package com.kwsni.caught_up.tvdb.batch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.EpisodeListResponseDto;

public class EpisodePagingItemReader extends AbstractPagingItemReader<EpisodeBaseRecordDto> {
        private String apiPath;
        private int nextPage;
        private Integer seriesId;
        private boolean seriesRemaining = false;
        private RestClient tvdbClient;
        private JdbcPagingItemReader<Integer> seriesIdReader;

        public EpisodePagingItemReader(JdbcPagingItemReader<Integer> seriesIdReader, RestClient tvdbClient) {
            this.seriesIdReader = seriesIdReader;
            this.tvdbClient = tvdbClient;
            this.nextPage = 0;
            setPageSize(500);
        }

        @Override
        public void doReadPage() {
            if(results == null) {
                results = new CopyOnWriteArrayList<>();
            } else {
                results.clear();
            }

            if(seriesId == null) {
                seriesId = readSeries();
                seriesRemaining = seriesId != null;
            }

            EpisodeListResponseDto episodeResponse;
            List<EpisodeBaseRecordDto> responseData;

            
            apiPath = String.format("/series/%d/episodes/default/eng", seriesId);
            
            if(logger.isDebugEnabled()) {
                logger.debug(String.format("fetching episodes for %d at page %d", seriesId, nextPage));
            }

            try {
                episodeResponse = fetchResponse();
                responseData = episodeResponse.data().episodes();
            } catch(HttpClientErrorException | HttpServerErrorException e) {
                logger.error(e);
                episodeResponse = null;
                responseData = null;
            }
            
            while(episodeResponse == null || responseData.isEmpty()) {
                if(logger.isDebugEnabled()) {
                    logger.debug("series has no episodes, skipping...");
                }

                seriesId = readSeries();
                if (seriesId == null) {
                    break;
                }

                apiPath = String.format("/series/%d/episodes/default/eng", seriesId);

                if(logger.isDebugEnabled()) {
                    logger.debug(String.format("fetching episodes for %d at page %d", seriesId, nextPage));
                }
                
                try {
                    episodeResponse = fetchResponse();
                    responseData = episodeResponse.data().episodes();
                } catch(HttpClientErrorException | HttpServerErrorException e) {
                    logger.error(e);
                    episodeResponse = null;
                    responseData = null;
                }
            }
            if(episodeResponse.links().next() != null) {
                nextPage = Integer.parseInt(episodeResponse.links().next().getQuery().split("=")[1]);
            } else {
                nextPage = 0;
            }
            if(logger.isDebugEnabled()) {
                logger.debug(String.format("actual page size: %d", responseData.size()));
                logger.debug(String.format("wanted page size: %d", episodeResponse.links().pageSize()));
            }
            if(responseData.size() <= episodeResponse.links().pageSize()) {
                seriesId = readSeries();
                seriesRemaining = seriesId != null;
                if(seriesRemaining) {
                    setPageSize(responseData.size());
                } else {
                    setPageSize(episodeResponse.links().pageSize());
                }
            } else {
                setPageSize(episodeResponse.links().pageSize());
            }
            if(logger.isDebugEnabled()) {
                logger.debug(String.format("final page size: %d", getPageSize()));
                logger.debug(String.format("continue?: %B", seriesRemaining));
            }
            results.addAll(responseData);
        }

        @Retryable(
            maxRetries = 4,
            delay = 1000,
            jitter = 10,
            multiplier = 2,
            maxDelay = 5000
        )
        private EpisodeListResponseDto fetchResponse() {
            return tvdbClient.get()
                .uri(apiPath, uriBuilder -> uriBuilder.queryParam("page", nextPage)
                    .build())
                .retrieve()
                .body(EpisodeListResponseDto.class);
        }

        private Integer readSeries() {
            try {
                if(logger.isDebugEnabled()) {
                    logger.debug("reading new series id");
                }
                return seriesIdReader.read();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return Integer.valueOf(-1);
        }
}
