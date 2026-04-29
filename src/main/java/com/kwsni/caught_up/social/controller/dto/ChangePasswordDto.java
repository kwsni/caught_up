package com.kwsni.caught_up.social.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
    @NotBlank
    String currentPassword,
    @NotBlank
    String newPassword,
    @NotBlank
    String confirmPassword
) {}
