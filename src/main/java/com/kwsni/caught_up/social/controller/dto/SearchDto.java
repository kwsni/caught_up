package com.kwsni.caught_up.social.controller.dto;

public record SearchDto(
    String query,
    int page,
    int pageSize
) {
    
}
