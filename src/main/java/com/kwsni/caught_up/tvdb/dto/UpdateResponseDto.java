package com.kwsni.caught_up.tvdb.dto;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;

public record UpdateResponseDto(
    List<UpdateDto> data,
    String status,
    Links links
) {
    public record UpdateDto(
        String entityType,
        Integer methodInt,
        String method,
        String extraInfo,
        Integer userId,
        String recordType,
        Long recordId,
        Long timeStamp,
        Long seriesId,
        // Only present for episodes records:
        @Nullable
        Long mergeToId,
        @Nullable
        String mergeToType
    ) {}

    public record Links(
        @Nullable
        URL prev,
        URL self,
        @Nullable
        URL next,
        @JsonProperty("total_items")
        Integer totalItems,
        @JsonProperty("page_size")
        Integer pageSize
    ) {} 
}
