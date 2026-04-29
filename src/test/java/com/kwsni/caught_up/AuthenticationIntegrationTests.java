package com.kwsni.caught_up;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.kwsni.caught_up.social.controller.dto.UserRegistrationDto;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AuthenticationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    static UserRegistrationDto validForm;
    static UserRegistrationDto existingUsernameForm;
    static UserRegistrationDto existingEmailForm;
    static UserRegistrationDto invalidEmailForm;
    static UserRegistrationDto passwordMismatchForm;

    @BeforeAll
    static void setupForms() {
        validForm = new UserRegistrationDto(
            "user1@email.com",
            "user1",
            "password123",
            "password123"
        );
        existingUsernameForm = new UserRegistrationDto(
            "user.one@email.com",
            "user1",
            "password123",
            "password123"
        );
        existingEmailForm = new UserRegistrationDto(
            "user1@email.com",
            "user_one",
            "password123",
            "password123"
        );
        invalidEmailForm = new UserRegistrationDto(
            "user.one.email.com",
            "user1",
            "password123",
            "password123"
        );
        passwordMismatchForm = new UserRegistrationDto(
            "user2@email.com",
            "user2",
            "password123",
            "password456"
        );
    }

    @WithAnonymousUser
    @Test
    void testValidRegistration() throws Exception {
        mockMvc.perform(post("/create-account").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("email", validForm.email())
            .param("username", validForm.username())
            .param("password", validForm.password())
            .param("confirmPassword", validForm.confirmPassword()))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/sign-in"));
    }

    @WithAnonymousUser
    @Test
    void testExistingUsernameRegistration() throws Exception {
        mockMvc.perform(post("/create-account").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("email", existingUsernameForm.email())
            .param("username", existingUsernameForm.username())
            .param("password", existingUsernameForm.password())
            .param("confirmPassword", existingUsernameForm.confirmPassword())
        ).andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("user", "username"))
        .andExpect(model().attributeHasFieldErrors("user", "email"))
        .andExpect(view().name("registration"));
    }

    @WithAnonymousUser
    @Test
    void testExistingEmailRegistration() throws Exception {
        mockMvc.perform(post("/create-account").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("email", existingEmailForm.email())
            .param("username", existingEmailForm.username())
            .param("password", existingEmailForm.password())
            .param("confirmPassword", existingEmailForm.confirmPassword())
        ).andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("user", "username"))
        .andExpect(model().attributeHasFieldErrors("user", "email"))
        .andExpect(view().name("registration"));
    }

    @WithAnonymousUser
    @Test
    void testInvalidEmailRegistration() throws Exception {
        mockMvc.perform(post("/create-account").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("email", invalidEmailForm.email())
            .param("username", invalidEmailForm.username())
            .param("password", invalidEmailForm.password())
            .param("confirmPassword", invalidEmailForm.confirmPassword())
        ).andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("user", "email"))
        .andExpect(view().name("registration"));
    }

    @WithAnonymousUser
    @Test
    void testMismatchingPasswordRegistration() throws Exception {
        mockMvc.perform(post("/create-account").contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .param("email", passwordMismatchForm.email())
            .param("username", passwordMismatchForm.username())
            .param("password", passwordMismatchForm.password())
            .param("confirmPassword", passwordMismatchForm.confirmPassword())
        ).andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("user", "confirmPassword"))
        .andExpect(view().name("registration"));
    }
}
