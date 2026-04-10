package com.kwsni.caught_up.tvdb.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.kwsni.caught_up.tvdb.batch.model.UpdateRecord;
import com.kwsni.caught_up.tvdb.dto.UpdateResponseDto.UpdateDto;

public class UpdateProcessor implements ItemProcessor<UpdateDto, UpdateRecord> {
    private Log logger = LogFactory.getLog(getClass());
    @Override
    public UpdateRecord process(UpdateDto updateDto) throws Exception {
        if(logger.isDebugEnabled()) {
            logger.debug("Processing update record for " + updateDto.entityType() + " with id " + updateDto.recordId() + " and method " + updateDto.methodInt());
        }

        UpdateRecord update = new UpdateRecord(
            updateDto.entityType(),
            updateDto.methodInt(),
            updateDto.method(),
            updateDto.recordId(),
            updateDto.mergeToId(),
            updateDto.mergeToType()
        );
        
        return update;
    }
}
