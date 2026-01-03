package com.kwsni.caught_up.tvdb.dto;

public record EpisodeResponseDto(
    EpisodeBaseRecordDto data,
    String status
) {}
