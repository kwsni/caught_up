package com.kwsni.caught_up.tvdb.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;

public class SeriesEpisodeItemReader implements ItemReader<EpisodeBaseRecordDto> {

    private JdbcPagingItemReader<Integer> seriesIdReader;
    private EpisodePagingItemReader episodeReader;
    private RestClient tvdbClient;
    private Integer seriesId;
    private EpisodeBaseRecordDto result;
    
    private Log logger = LogFactory.getLog(getClass());
    
    public SeriesEpisodeItemReader(JdbcPagingItemReader<Integer> seriesIdReader, RestClient tvdbClient) {
        this.result = null;
        this.seriesIdReader = seriesIdReader;
        this.tvdbClient = tvdbClient;
    }

    @Override
    public EpisodeBaseRecordDto read() throws Exception {
        if(seriesId == null) {
            seriesId = seriesIdReader.read();
            episodeReader = new EpisodePagingItemReader(tvdbClient, seriesId);
        }
        if(seriesId != null) {
            result = episodeReader.read();
            while(result == null) {
                seriesId = seriesIdReader.read();
                episodeReader = new EpisodePagingItemReader(tvdbClient, seriesId);
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
