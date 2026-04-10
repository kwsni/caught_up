package com.kwsni.caught_up.social.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record PostReviewDto(
    @NotBlank
    String content,
    @PastOrPresent
    LocalDate watchedOn,
    @Positive
    Double rating,
    Boolean isSpoiler,
    Boolean like) {
    
}
