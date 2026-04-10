package com.kwsni.caught_up.tvdb.batch;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;

import com.kwsni.caught_up.tvdb.batch.repository.UpdateRecordRepository;

public class UpdateJobExecutionListener implements JobExecutionListener{
    private final UpdateRecordRepository updateRecordRepository;

    public UpdateJobExecutionListener(UpdateRecordRepository updateRecordRepository) {
        this.updateRecordRepository = updateRecordRepository;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        updateRecordRepository.deleteAll();
    }
}
