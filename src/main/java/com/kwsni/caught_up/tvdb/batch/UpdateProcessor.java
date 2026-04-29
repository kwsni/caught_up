package com.kwsni.caught_up.tvdb.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.UpdateDto;

public class UpdateProcessor implements ItemProcessor<UpdateDto, UpdateRecord> {
    private JobExecution jobExecution;
    private Log logger = LogFactory.getLog(getClass());
    @Override
    public UpdateRecord process(UpdateDto updateDto) throws Exception {
        if(logger.isDebugEnabled()) {
            logger.debug("Processing update record for " + updateDto.entityType() + " with id " + updateDto.recordId() + " and method " + updateDto.methodInt());
        }

        if(updateDto.entityType().equals("series") || updateDto.entityType().equals("episodes")) {
            UpdateRecord update = new UpdateRecord(
                updateDto.entityType(),
                updateDto.methodInt(),
                updateDto.method(),
                updateDto.recordId(),
                updateDto.mergeToId(),
                updateDto.mergeToType(),
                updateDto.timeStamp()
            );
            return update;
        }
        else {
            return null;
        }
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        this.jobExecution = jobExecution;
    }

    @BeforeProcess
    public void beforeProcess(UpdateDto item) {
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        jobContext.putString("lastReadUpdated", String.valueOf(item.timeStamp()));
    }
}
