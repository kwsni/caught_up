package com.kwsni.caught_up.tvdb.batch;

import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;

import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;

public class SeriesEpisodeItemReader implements ItemReader<EpisodeBaseRecordDto> {
    private TvdbService tvdbSvc;
    private JdbcPagingItemReader<Long> seriesIdReader;
    private EpisodePagingItemReader episodeReader;
    private Long seriesId;
    private EpisodeBaseRecordDto result;
    
    public SeriesEpisodeItemReader(TvdbService tvdbSvc, JdbcPagingItemReader<Long> seriesIdReader) {
        this.result = null;
        this.tvdbSvc = tvdbSvc;
        this.seriesIdReader = seriesIdReader;
    }

    @Override
    public EpisodeBaseRecordDto read() throws Exception {
        if(seriesId == null) {
            seriesId = seriesIdReader.read();
            episodeReader = new EpisodePagingItemReader(tvdbSvc, seriesId);
        }
        if(seriesId != null) {
            result = episodeReader.read();
            while(result == null) {
                seriesId = seriesIdReader.read();
                episodeReader = new EpisodePagingItemReader(tvdbSvc, seriesId);
                if(seriesId != null) {
                    result = episodeReader.read();
                } else {
                    return result;
                }
            }
        } else {
            result = null;
        }
        return result;
    }
}
