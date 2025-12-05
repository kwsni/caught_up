package com.kwsni.caught_up.tvdb.dto;

public record TvdbLoginResponseDto(Body data, String status) {
    public record Body(String token) {}
}