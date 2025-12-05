package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

public record EpisodeResponseDto(
    Data data,
    String status
) {
    public record Data(
        SeriesBaseRecordDto series,
        List<EpisodeBaseRecordDto> episodes
    ) {}
}
