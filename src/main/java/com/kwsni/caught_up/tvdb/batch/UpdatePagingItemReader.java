package com.kwsni.caught_up.tvdb.batch;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;
import org.springframework.data.redis.core.RedisTemplate;

import com.kwsni.caught_up.tvdb.batch.service.TvdbService;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.UpdateDto;

public class UpdatePagingItemReader extends AbstractPagingItemReader<UpdateDto> {
    private TvdbService tvdbSvc;
    private RedisTemplate<String, String> redisTemplate;
    private JobExecution jobExecution;

    private String lastUpdated;
    private int nextPage;

    public UpdatePagingItemReader(TvdbService tvdbSvc, RedisTemplate<String, String> redisTemplate) {
        this.nextPage = 0;
        this.tvdbSvc = tvdbSvc;
        this.redisTemplate = redisTemplate;
        setPageSize(500);
    }

    @Override
    public void doReadPage() {
        if(results == null) {
            results = new CopyOnWriteArrayList<>();
        } else {
            results.clear();
        }

        UpdateResponseDto updateResponse;
        List<UpdateDto> responseData;
        updateResponse = tvdbSvc.fetchUpdates(lastUpdated, nextPage);
        responseData = updateResponse.data();

        ExecutionContext jobContext = this.jobExecution.getExecutionContext();
        if(logger.isTraceEnabled()) {
            logger.trace("lastReadUpdated: " + String.valueOf(responseData.getLast()));
        }
        jobContext.putString("lastReadUpdated", String.valueOf(responseData.getLast().timeStamp()));

        if(updateResponse.links().next() != null) {
            nextPage = Integer.parseInt(updateResponse.links().next().getQuery().split("=")[2]);
        }
        results.addAll(responseData);
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.lastUpdated = jobContext.getString("lastUpdated");

        jobContext.putString("lastReadUpdated", jobContext.getString("lastUpdated"));
        this.jobExecution = jobExecution;
    }

    @OnReadError
    public void onReadError(Exception ex) {
        ExecutionContext jobContext = this.jobExecution.getExecutionContext();
        String lastReadUpdated = jobContext.getString("lastReadUpdated");
        redisTemplate.opsForValue().set("lastUpdated", lastReadUpdated);
    }
}
