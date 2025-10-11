package com.kwsni.caught_up.social.controller;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import com.kwsni.caught_up.social.dto.UserRegistrationDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.UserAccount;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.UserAccountRepository;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class AuthenticationController {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create-account")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserRegistrationDto registerDto = new UserRegistrationDto(
            "",
            "",
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
    public String registerUser(@ModelAttribute UserRegistrationDto registerDto,
        Errors errors,
        HttpServletRequest request,
        Model model) {
        // TODO: Error check, form validation
        // '1. Check for existing username
        // '2. Check if email is valid (regex and check for existing, confirmation later)
        // '3. Check if passwords are matching
        // '4. Persist DTO to entity

        UserAccount existingUserAccount = userAccountRepository.findByUsername(registerDto.username());
        UserAccount existingEmail = userAccountRepository.findByEmail(registerDto.email());

        if(existingUserAccount != null || existingEmail != null ) {
            return "redirect:/registration?error";
        }
        
        Pattern p = Pattern.compile("(.+)@(\\S+)");

        if(!p.matcher(registerDto.email()).matches()) {
            return "redirect:/registration?error";
        }

        String password = registerDto.password();
        String confirmPassword = registerDto.confirmPassword();

        if(!password.equals(confirmPassword)) {
            return "redirect:/registration?error";
        }

        String encodedPassword = passwordEncoder.encode(registerDto.password());

        Member newMember = new Member(
            registerDto.email(),
            registerDto.firstName(),
            registerDto.lastName(),
            registerDto.username(),
            encodedPassword,
            "user");
        memberRepository.save(newMember);

        return "redirect:/sign-in";
    }
    
}
