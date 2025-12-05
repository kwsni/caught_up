package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

import jakarta.annotation.Nullable;

public record EpisodeBaseRecordDto (
    Integer absoluteNumber,
    String aired,
    Integer airsAfterSeason,
    Integer airsBeforeEpisode,
    Integer airsBeforeSeason,
    String finaleType,
    Long id,
    String image,
    @Nullable
    Integer imageType,
    Long isMovie,
    String lastUpdated,
    Integer linkedMovie,
    String name,
    List<String> nameTranslations,
    Integer number,
    String overview,
    List<String> overviewTranslations,
    @Nullable
    Integer runtime,
    Integer seasonNumber,
    List<SeasonBaseRecordDto> seasons,
    Long seriesId,
    String seasonName,
    String year
) {}
