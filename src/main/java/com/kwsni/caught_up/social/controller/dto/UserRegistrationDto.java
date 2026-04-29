package com.kwsni.caught_up.social.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationDto (
     @Email
     @NotBlank
     String email,
     @NotBlank
     String username,
     @NotBlank
     String password,
     @NotBlank
     String confirmPassword
) {}
