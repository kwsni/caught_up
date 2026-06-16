package com.kwsni.caught_up.social.service;

import org.springframework.stereotype.Service;

import com.kwsni.caught_up.social.controller.dto.UserProfileDto;
import com.kwsni.caught_up.social.controller.dto.UserRegistrationDto;

import net.datafaker.Faker;

@Service
public class MemberGenerationService {
    private final Faker faker;

    public MemberGenerationService() {
        this.faker = new Faker();
    }

    public UserRegistrationDto generateUser() {
        var pwd = faker.credentials().password(8, 16, true, true, true);

        return new UserRegistrationDto(
            faker.internet().safeEmailAddress(),
            faker.credentials().username(),
            pwd,
            pwd
        );
    }

    public UserProfileDto generateProfile() {
        return new UserProfileDto(
            faker.name().firstName(),
            faker.name().lastName(),
            null,
            faker.expression("#{address.city}, #{address.state}"),
            null,
            null
        );
    }
}
