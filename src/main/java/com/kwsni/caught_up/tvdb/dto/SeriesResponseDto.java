package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

import jakarta.annotation.Nullable;

public record SeriesResponseDto(
    List<SeriesBaseRecordDto> data,
    String status,
    Links links
) {
    public record Links(
        @Nullable
        String prev,
        String self,
        @Nullable
        String next,
        Integer totalItems,
        Integer pageSize
    ) {}    
}

