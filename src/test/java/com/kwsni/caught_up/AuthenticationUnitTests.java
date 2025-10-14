package com.kwsni.caught_up;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;

import com.kwsni.caught_up.config.WebSecurityConfig;
import com.kwsni.caught_up.social.controller.AuthenticationController;
import com.kwsni.caught_up.social.dto.UserRegistrationDto;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.UserAccountRepository;
import com.kwsni.caught_up.social.service.UserAccountService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationUnitTests {
    
    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationController authenticationController;

    static UserRegistrationDto validForm;
    static UserRegistrationDto existingUsernameForm;
    static UserRegistrationDto existingEmailForm;
    static UserRegistrationDto invalidEmailForm;
    static UserRegistrationDto passwordMismatchForm;

    @BeforeAll
    static void setupForms() {
        validForm = new UserRegistrationDto(
            "user",
            "one",
            "user1@email.com",
            "user1",
            "password123",
            "password123"
        );
        existingUsernameForm = new UserRegistrationDto(
            "user",
            "one",
            "user.one@email.com",
            "user1",
            "password123",
            "password123"
        );
        existingEmailForm = new UserRegistrationDto(
            "user",
            "one",
            "user1@email.com",
            "user_one",
            "password123",
            "password123"
        );
        invalidEmailForm = new UserRegistrationDto(
            "user",
            "one",
            "user.one.email.com",
            "user1",
            "password123",
            "password123"
        );
        passwordMismatchForm = new UserRegistrationDto(
            "user",
            "two",
            "user2@email.com",
            "user2",
            "password123",
            "password456"
        );
    }


    @Test
    public void formValidation() throws Exception {
        String success = "redirect:/sign-in";
        
        String validFormResult = authenticationController.registerUser(validForm);
        //String existingUsernameFormResult = authenticationController.registerUser(existingUsernameForm);
        //String existingEmailFormResult = authenticationController.registerUser(existingEmailForm);
        String invalidEmailFormResult = authenticationController.registerUser(invalidEmailForm);
        String passwordMismatchFormResult = authenticationController.registerUser(passwordMismatchForm);
        
        assertThat(validFormResult).isEqualTo(success);
        //assertThat(existingUsernameFormResult).isNotEqualTo(success);
        //assertThat(existingEmailFormResult).isNotEqualTo(success);
        assertThat(invalidEmailFormResult).isNotEqualTo(success);
        assertThat(passwordMismatchFormResult).isNotEqualTo(success);
    }
}
