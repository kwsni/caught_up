package com.kwsni.caught_up.tvdb.batch;

import java.util.Date;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledSync {
    
    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job tvdbUpdateSyncJob;

    @Scheduled(cron = "23 43 * * * *")
    public void runUpdateSyncJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addDate("date", new Date())
            .toJobParameters();
        jobOperator.run(tvdbUpdateSyncJob, jobParameters);
    }
}
