package com.kwsni.caught_up.tvdb.dto;

public record SeriesResponseDto(
    SeriesBaseRecordDto data,
    String status
) {}
