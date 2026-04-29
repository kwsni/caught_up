package com.kwsni.caught_up.tvdb.batch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UpdateRecord {

    private String entityType;

    private Integer methodInt;

    private String method;

    @Id
    private Long recordId;

    private Long mergeToId;

    private String mergeToType;

    private Long timestamp;

    protected UpdateRecord() {}

    public UpdateRecord(String entityType, Integer methodInt, String method, Long recordId , Long mergeToId, String mergeToType, Long timestamp) {
        this.entityType = entityType;
        this.methodInt = methodInt;
        this.method = method;
        this.recordId = recordId;
        this.mergeToId = mergeToId;
        this.mergeToType = mergeToType;
        this.timestamp = timestamp;
    }

    public String getEntityType() {
        return entityType;
    }

    public Integer getMethodInt() {
        return methodInt;
    }

    public String getMethod() {
        return method;
    }

    public Long getRecordId() {
        return recordId;
    }

    public Long getMergeToId() {
        return mergeToId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setMethodInt(Integer methodInt) {
        this.methodInt = methodInt;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setMergeToId(Long mergeToId) {
        this.mergeToId = mergeToId;
    }

    public String getMergeToType() {
        return mergeToType;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public void setMergeToType(String mergeToType) {
        this.mergeToType = mergeToType;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
