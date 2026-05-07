package com.kwsni.caught_up.tvdb.batch;

import java.util.Date;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ScheduledSync {
    private final JobOperator jobOperator;
    private final Job tvdbUpdateSyncJob;

    public ScheduledSync(JobOperator jobOperator, Job tvdbUpdateSyncJob) {
        this.jobOperator = jobOperator;
        this.tvdbUpdateSyncJob = tvdbUpdateSyncJob;
    }
    
    @Scheduled(cron = "23 43 * * * *")
    public void runUpdateSyncJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addDate("date", new Date())
            .toJobParameters();
        jobOperator.run(tvdbUpdateSyncJob, jobParameters);
    }
}
