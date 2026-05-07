package com.kwsni.caught_up.social.controller;

import java.security.Principal;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.kwsni.caught_up.social.controller.dto.ChangePasswordDto;
import com.kwsni.caught_up.social.controller.dto.UserProfileDto;
import com.kwsni.caught_up.social.controller.dto.UserRegistrationDto;
import com.kwsni.caught_up.social.service.MemberService;
import com.kwsni.caught_up.social.service.MemberService.PasswordNotConfirmedException;
import com.kwsni.caught_up.social.service.MemberService.UserAlreadyExistsException;



@Controller
public class AuthenticationController {
    private final MemberService memberSvc;

    public AuthenticationController(MemberService memberSvc) {
        this.memberSvc = memberSvc;
    }

    @GetMapping("/create-account")
    public String showRegistrationForm(Model model) {
        var registerDto = memberSvc.createRegistrationForm();

        model.addAttribute("user", registerDto);
        return "registration";
    }

    @GetMapping("/sign-in")
    public String showSignInForm() {
        return "sign-in";
    }

    @PostMapping("/create-account")
    public String registerUser(
        @ModelAttribute("user") UserRegistrationDto registerDto,
        BindingResult bindingResult,
        Model model
    ) {
        if(bindingResult.hasErrors()) {
            return "registration";
        }
        try {
            memberSvc.createUser(registerDto, false);
            return "redirect:/sign-in";
        } catch(UserAlreadyExistsException e) {
            bindingResult.rejectValue("username", null, e.getMessage());
            bindingResult.rejectValue("email", null, e.getMessage());

            model.addAttribute("user", registerDto);
            return "registration";
        } catch(PasswordNotConfirmedException e) {
            bindingResult.rejectValue("confirmPassword", "confirm.password.mismatch", e.getMessage());

            model.addAttribute("user", registerDto);
            return "registration";
        }
    }
    
    @GetMapping("/settings")
    public String accountSettings(
        Model model,
        Principal principal
    ) {
        var username = principal.getName();
        var profileDto = memberSvc.getProfile(username);

        model.addAttribute("profileDto", profileDto);
        return "settings";
    }

    @PostMapping("/settings")
    public String changeAccountSettings(
        @ModelAttribute UserProfileDto profileDto,
        Principal principal
    ) {
        var username = principal.getName();

        memberSvc.modifyProfile(profileDto, username);
        return "redirect:/members/" + username;
    }
    
    @GetMapping("/settings/password")
    public String passwordPage(Model model) {
        var passwordDto = memberSvc.createPasswordForm();

        model.addAttribute("passwordDto", passwordDto);
        return "password";
    }

    @PostMapping("/settings/password")
    public String changePassword(
        @ModelAttribute ChangePasswordDto passwordDto,
        BindingResult bindingResult,
        Principal principal
    ) {
        var username = principal.getName();
        
        if(bindingResult.hasErrors()) {
            return "password";
        }
        try {
            memberSvc.updatePassword(
                username,
                passwordDto
            );
            return "redirect:/members/" + username;
        } catch(PasswordNotConfirmedException e) {
            bindingResult.rejectValue("confirmPassword", null, e.getMessage());
            return "password";
        } catch(BadCredentialsException e) {
            bindingResult.rejectValue("currentPassword", null, e.getMessage());
            return "password";
        }
    }
}
