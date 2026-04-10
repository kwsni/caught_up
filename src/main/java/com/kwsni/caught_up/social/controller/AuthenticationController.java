package com.kwsni.caught_up.social.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.kwsni.caught_up.social.dto.ChangePasswordDto;
import com.kwsni.caught_up.social.dto.UserProfileDto;
import com.kwsni.caught_up.social.dto.UserRegistrationDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.service.UserAccountService;
import com.kwsni.caught_up.social.service.UserAccountService.PasswordNotConfirmedException;
import com.kwsni.caught_up.social.service.UserAccountService.UserAlreadyExistsException;



@Controller
public class AuthenticationController {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/create-account")
    public String showRegistrationForm(Model model) {
        UserRegistrationDto registerDto = new UserRegistrationDto(
            "",
            "",
            "",
            ""
        );
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
            userAccountService.createUser(
                registerDto.username(),
                registerDto.email(),
                registerDto.password(),
                registerDto.confirmPassword()
            );
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
    public String accountSettings(Model model, Principal principal) {
        Member profileMember = memberRepository.findByUsername(principal.getName());
        UserProfileDto profileDto = new UserProfileDto(
            profileMember.getAvatar(),
            profileMember.getFirstName(),
            profileMember.getLastName(),
            profileMember.getBio(),
            profileMember.getLocation(),
            profileMember.getWebsite(),
            profileMember.getPronoun()
        );

        model.addAttribute("profileDto", profileDto);
        return "settings";
    }

    @PostMapping("/settings")
    public String changeAccountSettings(
        @ModelAttribute UserProfileDto profileDto,
        Principal principal
    ) {
        Member profileMember = memberRepository.findByUsername(principal.getName());
        
        profileMember.setAvatar(profileDto.avatar());
        profileMember.setFirstName(profileDto.firstName());
        profileMember.setLastName(profileDto.lastName());
        profileMember.setBio(profileDto.bio());
        profileMember.setLocation(profileDto.location());
        profileMember.setWebsite(profileDto.website()       .replace("http://", "")
            .replace("https://", "")
        );

        memberRepository.save(profileMember);
        
        return "redirect:/members/" + principal.getName();
    }
    
    @GetMapping("/settings/password")
    public String passwordPage(Model model) {
        ChangePasswordDto passwordDto = new ChangePasswordDto(
            null,
            null,
            null
        );

        model.addAttribute("passwordDto", passwordDto);
        return "password";
    }

    @PostMapping("/settings/password")
    public String changePassword(
        @ModelAttribute ChangePasswordDto passwordDto,
        BindingResult bindingResult,
        Principal principal
    ) {
        if(bindingResult.hasErrors()) {
            return "password";
        }
        try {
            userAccountService.updatePassword(
                principal.getName(),
                passwordDto.currentPassword(),
                passwordDto.newPassword(),
                passwordDto.confirmPassword()
            );
            return "redirect:/members/" + principal.getName();
        } catch(PasswordNotConfirmedException e) {
            bindingResult.rejectValue("confirmPassword", null, e.getMessage());
            return "password";
        } catch(BadCredentialsException e) {
            bindingResult.rejectValue("currentPassword", null, e.getMessage());
            return "password";
        }
    }
}
