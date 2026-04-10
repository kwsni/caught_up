package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

import jakarta.annotation.Nullable;

public record CompanyDto(
    String activeDate,
    List<AliasDto> aliases,
    String country,
    Long id,
    String inactiveDate,
    String name,
    List<String> nameTranslations,
    List<String> overviewTranslations,
    @Nullable
    Long primaryCompanyType,
    String slug,
    ParentCompany parentCompany,
    TagOption tagOptions
) {
    public record ParentCompany(
        @Nullable
        Long id,
        String name,
        CompanyRelationShip relation
    ) {
        public record CompanyRelationShip(
            @Nullable
            Integer id,
            String typeName
        ) {}
    }
    public record TagOption(
        String helpText,
        Long id,
        String name,
        Long tag,
        String tagName
    ) {}
}
