package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record EpisodeBaseRecordDto (
    Integer absoluteNumber,
    String aired,
    Integer airsAfterSeason,
    Integer airsBeforeEpisode,
    Integer airsBeforeSeason,
    String finaleType,
    Long id,
    String image,
    Integer imageType,
    Long isMovie,
    String lastUpdated,
    Integer linkedMovie,
    String name,
    List<String> nameTranslations,
    Integer number,
    String overview,
    List<String> overviewTranslations,
    Integer runtime,
    Integer seasonNumber,
    List<SeasonBaseRecordDto> seasons,
    Integer seriesId,
    String seasonName,
    String year
) {
    public EpisodeBaseRecordDto {
        if(overview != null && overview.contains("\u0000")) {
            overview = overview.replace("\u0000", "");
        }
        if(image != null && image.contains("https://artworks.thetvdb.com")) {
            image = image.replace("https://artworks.thetvdb.com", "");
        }
    }
}
