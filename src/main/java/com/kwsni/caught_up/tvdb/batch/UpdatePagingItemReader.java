package com.kwsni.caught_up.tvdb.batch;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestClient;

import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.UpdateDto;

public class UpdatePagingItemReader extends AbstractPagingItemReader<UpdateDto> {
    private final String apiPath;
    private int nextPage;
    private RestClient tvdbClient;
    private RedisTemplate<String, String> redisTemplate;

    public UpdatePagingItemReader(RestClient tvdbClient, RedisTemplate<String, String> redisTemplate) {
        this.apiPath = "/updates";
        this.nextPage = 0;
        this.tvdbClient = tvdbClient;
        this.redisTemplate = redisTemplate;
        setPageSize(500);
    }

    @BeforeStep
    public void cacheLastUpdate(StepExecution stepExecution) {
        stepExecution.getExecutionContext().put("lastUpdated", Instant.now().getEpochSecond());
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
        updateResponse = fetchResponse();
        responseData = updateResponse.data();
        
        if(updateResponse.links().next() != null) {
            nextPage = Integer.parseInt(updateResponse.links().next().getQuery().split("=")[2]);
        }
        results.addAll(responseData);
    }

    private UpdateResponseDto fetchResponse() {
        String lastUpdated = redisTemplate.opsForValue().get("lastUpdated");
        return tvdbClient.get()
            .uri(apiPath, uriBuilder -> uriBuilder
                .queryParam("since", lastUpdated)
                .queryParam("page", nextPage)
                .build())
            .retrieve()
            .body(UpdateResponseDto.class);
    }

    // Move to AfterJob?
    @AfterStep
    ExitStatus setLastUpdate(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();
        if(exitCode.equals(ExitStatus.COMPLETED.getExitCode())) {
            String lastUpdated = String.valueOf(stepExecution.getExecutionContext().get("lastUpdated", Long.class));
            logger.debug("ctx lastupdated: " + lastUpdated);
            redisTemplate.opsForValue().set(
                "lastUpdated",
                lastUpdated);
        }
        return stepExecution.getExitStatus();
    }
}
