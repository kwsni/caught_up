package com.kwsni.caught_up.tvdb.batch;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.EpisodeBaseRecordDto;
import com.kwsni.caught_up.tvdb.dto.TransResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateRecordDto;
import com.kwsni.caught_up.tvdb.model.Episode;

public class DeleteEpisodeProcessor implements ItemProcessor<UpdateRecord, UpdateRecordDto> {
    private TvdbService tvdbSvc;
    private Log logger = LogFactory.getLog(getClass());

    public DeleteEpisodeProcessor(TvdbService tvdbSvc) {
        this.tvdbSvc = tvdbSvc;
    }

    @Override
    public UpdateRecordDto process(UpdateRecord update) throws Exception {
        Long mergeToId = update.getMergeToId();
        Long recordId = update.getRecordId();
        int methodInt = update.getMethodInt();
        String mergeToType = update.getMergeToType();

        if(logger.isDebugEnabled()) {
            logger.debug("Processing update record for episode with id " + recordId + " and method " + methodInt);
        }

        Optional<Episode> episode = Optional.empty();
        
        if(mergeToId != null && mergeToType.equals("episodes")) {
            EpisodeBaseRecordDto episodeDto = tvdbSvc.fetchEpisode(mergeToId).data();

            boolean hasNameTrans = episodeDto.nameTranslations().contains("eng");
            boolean hasOverviewTrans = episodeDto.overviewTranslations().contains("eng");
            boolean hasTrans =  hasNameTrans || hasOverviewTrans;

            if(hasTrans) {
                if(logger.isTraceEnabled()) {
                    logger.trace("Fetching episode trans for " + mergeToId + " hasNameTrans: " + episodeDto.nameTranslations() + hasNameTrans + " hasOverviewTrans: " +  episodeDto.overviewTranslations() + hasOverviewTrans);
                }
                TransResponseDto.Data translation = tvdbSvc.fetchTrans(mergeToType, mergeToId).data();
                episode = Optional.of(tvdbSvc.mapToEpisode(episodeDto, translation));
            } else {
                episode = Optional.of(tvdbSvc.mapToEpisode(episodeDto));
            }
        }

        return new UpdateRecordDto(
            Optional.empty(),
            episode,
            Optional.of(recordId)
        );
    }
}
