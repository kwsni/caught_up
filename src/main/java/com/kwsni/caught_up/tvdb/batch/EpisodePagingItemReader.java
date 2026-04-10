package com.kwsni.caught_up.tvdb.batch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesEpisodeListResponseDto;

public class EpisodePagingItemReader extends AbstractPagingItemReader<EpisodeBaseRecordDto> {
        private final String apiPath;
        private int nextPage;
        private RestClient tvdbClient;

        public EpisodePagingItemReader(RestClient tvdbClient, Integer seriesId) {
            this.apiPath = "/series/" + seriesId + "/episodes/default/eng";
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
            
            SeriesEpisodeListResponseDto episodeResponse;
            List<EpisodeBaseRecordDto> responseData;
            if(logger.isDebugEnabled()) {
                logger.debug("episodeReader calling fetchResponse() at path " + apiPath);
            }
            episodeResponse = fetchResponse();
            responseData = episodeResponse.data().episodes();

            nextPage++;
            setPageSize(episodeResponse.links().pageSize());
            results.addAll(responseData);
        }

        private SeriesEpisodeListResponseDto fetchResponse() {
            return tvdbClient.get()
                .uri(apiPath, uriBuilder -> uriBuilder.queryParam("page", nextPage)
                    .build())
                .retrieve()
                .body(SeriesEpisodeListResponseDto.class);
        }
}