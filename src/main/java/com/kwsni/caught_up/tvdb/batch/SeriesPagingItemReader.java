package com.kwsni.caught_up.tvdb.batch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;

import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.SeriesBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.SeriesListResponseDto;

public class SeriesPagingItemReader extends AbstractPagingItemReader<SeriesBaseRecordDto> {
    private final TvdbService tvdbSvc;
    private int nextPage;

    public SeriesPagingItemReader(TvdbService tvdbSvc) {
        this.tvdbSvc = tvdbSvc;
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

        SeriesListResponseDto seriesResponse;
        List<SeriesBaseRecordDto> responseData;
        seriesResponse = tvdbSvc.fetchSeriesList(nextPage);
        responseData = seriesResponse.data();
        
        if(seriesResponse.links().next() != null) {
            nextPage = Integer.parseInt(seriesResponse.links().next().getQuery().split("=")[1]);
        }
        setPageSize(seriesResponse.links().pageSize());
        results.addAll(responseData);
    }
}
