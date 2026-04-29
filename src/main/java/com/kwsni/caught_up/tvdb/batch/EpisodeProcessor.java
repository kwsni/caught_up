package com.kwsni.caught_up.tvdb.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.model.Episode;

public class EpisodeProcessor implements ItemProcessor<EpisodeBaseRecordDto, Episode> {
    private TvdbService tvdbSvc;
    private Log logger = LogFactory.getLog(getClass());

    public EpisodeProcessor(TvdbService tvdbSvc) {
        this.tvdbSvc = tvdbSvc;
    }

    @Override
    public Episode process(EpisodeBaseRecordDto episodeDto) throws Exception {
        if(logger.isDebugEnabled()) {
            logger.debug("Processing episode from series " + episodeDto.seriesId() + ": " + episodeDto.id() + " - S" + episodeDto.seasonNumber() + " E" + episodeDto.number());
        }
        return tvdbSvc.mapToEpisode(episodeDto);
    }
}
