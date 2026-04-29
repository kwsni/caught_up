package com.kwsni.caught_up.tvdb.batch;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.data.redis.core.RedisTemplate;

public class UpdateJobExecutionListener implements JobExecutionListener{
    private final RedisTemplate<String, String> redisTemplate;

    public UpdateJobExecutionListener(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().put("lastUpdated", redisTemplate.opsForValue().get("lastUpdated"));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String lastUpdated = String.valueOf(jobExecution.getExecutionContext().getString("lastReadUpdated"));
        redisTemplate.opsForValue().set("lastUpdated", lastUpdated);
    }
}
