package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

public record SeasonBaseRecordDto(
    Integer id,
    String image,
    Integer imageType,
    String lastUpdated,
    String name,
    List<String> nameTranslations,
    Long number,
    List<String> overviewTranslations,
    //List<Companies> companies,
    Long seriesId,
    SeasonType type,
    String year

) {
    public record Companies(
        List<CompanyDto> studio,
        List<CompanyDto> network,
        List<CompanyDto> production,
        List<CompanyDto> distributor,
        List<CompanyDto> special_effects
    ) {}
    public record SeasonType(
        String alternateName,
        Long id,
        String name,
        String type
    ) {}
}
