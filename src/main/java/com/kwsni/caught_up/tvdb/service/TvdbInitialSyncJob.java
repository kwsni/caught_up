package com.kwsni.caught_up.tvdb.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TvdbInitialSyncJob {
    
    private final TvdbSyncService tvdbSyncService;

    public TvdbInitialSyncJob(TvdbSyncService tvdbSyncService) {
        this.tvdbSyncService = tvdbSyncService;
    }

    public void initialTvdbSyncJob() {
        tvdbSyncService.initialTvdbSync();
    }
}
