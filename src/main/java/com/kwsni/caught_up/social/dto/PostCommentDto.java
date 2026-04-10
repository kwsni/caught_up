package com.kwsni.caught_up.social.dto;

import jakarta.validation.constraints.NotBlank;

public record PostCommentDto(@NotBlank String content) {}
