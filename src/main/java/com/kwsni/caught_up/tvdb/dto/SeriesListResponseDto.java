package com.kwsni.caught_up.tvdb.dto;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;

public record SeriesListResponseDto(
    List<SeriesBaseRecordDto> data,
    String status,
    Links links
) {
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

