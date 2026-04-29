package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

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
    Long seriesId,
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
