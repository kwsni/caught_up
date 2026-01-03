package com.kwsni.caught_up.tvdb.batch;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesListResponseDto;

public class SeriesPagingItemReader extends AbstractPagingItemReader<SeriesBaseRecordDto> {
        private final String apiPath;
        private int nextPage;
        private RestClient tvdbClient;
        private RedisTemplate<String, String> redisTemplate;

        public SeriesPagingItemReader(RestClient tvdbClient, RedisTemplate<String, String> redisTemplate) {
            this.tvdbClient = tvdbClient;
            this.redisTemplate = redisTemplate;
            this.apiPath = "/series";
            this.nextPage = 0;
            setPageSize(500);
        }

        @BeforeStep
        public void setLastUpdated(StepExecution stepExecution) {
            redisTemplate.opsForValue().set("lastUpdated", String.valueOf(Instant.now().getEpochSecond()));
        }

        @Override
        public void doReadPage() {
            if(results == null) {
                results = new CopyOnWriteArrayList<>();
            } else {
                results.clear();
            }

            SeriesListResponseDto seriesResponse;
            List<SeriesBaseRecordDto> responseData;
            seriesResponse = fetchResponse();
            responseData = seriesResponse.data();
            
            if(seriesResponse.links().next() != null) {
                nextPage = Integer.parseInt(seriesResponse.links().next().getQuery().split("=")[1]);
            }
            setPageSize(seriesResponse.links().pageSize());
            results.addAll(responseData);
        }

        private SeriesListResponseDto fetchResponse() {
            return tvdbClient.get()
                .uri(apiPath, uriBuilder -> uriBuilder.queryParam("page", nextPage)
                    .build())
                .retrieve()
                .body(SeriesListResponseDto.class);
        }
}
