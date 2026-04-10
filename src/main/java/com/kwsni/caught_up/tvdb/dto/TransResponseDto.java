package com.kwsni.caught_up.tvdb.dto;

import java.util.List;

import jakarta.annotation.Nullable;

public record TransResponseDto(
    Data data,
    String status
) {
    public record Data(
        String name,
        String overview,
        @Nullable
        String tagline,
        @Nullable
        String language,
        @Nullable
        Boolean isAlias,
        @Nullable
        Boolean isPrimary,
        @Nullable
        List<String> aliases
    ) {
        public Data {
        if(overview != null && overview.contains("\u0000")) {
            overview = overview.replace("\u0000", "");
        }
    }
    }
}
