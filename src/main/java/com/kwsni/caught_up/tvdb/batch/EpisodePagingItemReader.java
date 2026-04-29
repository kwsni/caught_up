package com.kwsni.caught_up.tvdb.batch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;

import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesEpisodeListResponseDto;

public class EpisodePagingItemReader extends AbstractPagingItemReader<EpisodeBaseRecordDto> {
    private TvdbService tvdbSvc;
    private Long seriesId;
    private int nextPage;

    public EpisodePagingItemReader(TvdbService tvdbSvc, Long seriesId) {
        this.tvdbSvc = tvdbSvc;
        this.seriesId = seriesId;
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
        episodeResponse = tvdbSvc.fetchSeriesEpisodeList(seriesId, nextPage);
        responseData = episodeResponse.data().episodes();

        nextPage++;
        setPageSize(episodeResponse.links().pageSize());
        results.addAll(responseData);
    }
}