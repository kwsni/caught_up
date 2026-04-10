package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

import jakarta.annotation.Nullable;

public record SeriesBaseRecordDto(
    List<AliasDto> aliases,
    @Nullable
    Integer averageRuntime,
    String country,
    Long defaultSeasonType,
    List<EpisodeBaseRecordDto> episodes,
    String firstAired,
    Long id,
    String image,
    Boolean isOrderRandomized,
    String lastAired,
    String lastUpdated,
    String name,
    List<String> nameTranslations,
    String nextAired,
    String originalCountry,
    String originalLanguage,
    List<String> overviewTranslations,
    String overview,
    Double score,
    String slug,
    Status status,
    String year
) {
    public SeriesBaseRecordDto {
        if(overview != null && overview.contains("\u0000")) {
            overview = overview.replace("\u0000", "");
        }
        if(image != null && image.contains("https://artworks.thetvdb.com")) {
            image = image.replace("https://artworks.thetvdb.com", "");
        }
    }

    public record Status(
        Long id,
        Boolean keepUpdated,
        String name,
        String recordType
    ) {}
}
