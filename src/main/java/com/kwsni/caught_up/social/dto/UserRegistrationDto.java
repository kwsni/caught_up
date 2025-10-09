package com.kwsni.caught_up.social.dto;

public record UserRegistrationDto (
     String firstName,
     String lastName,
     String email,
     String username,
     String password,
     String confirmPassword
) {}
