package com.kwsni.caught_up.social.controller.dto;

import org.hibernate.validator.constraints.URL;

import com.kwsni.caught_up.social.model.Pronoun;

public record UserProfileDto(
    String avatar,
    String firstName,
    String lastName,
    String bio,
    String location,
    @URL
    String website,
    Pronoun pronoun
) {}
