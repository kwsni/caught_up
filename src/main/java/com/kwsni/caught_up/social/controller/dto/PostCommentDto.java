package com.kwsni.caught_up.social.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PostCommentDto(@NotBlank String content) {}
