package com.kwsni.caught_up.tvdb.batch;

import java.time.Instant;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.data.redis.core.RedisTemplate;

public class TimestampJobExecutionListener implements JobExecutionListener {
    private final RedisTemplate<String, String> redisTemplate;

    public TimestampJobExecutionListener(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
            redisTemplate.opsForValue().set("lastUpdated", String.valueOf(Instant.now().getEpochSecond()));
        }    
}
