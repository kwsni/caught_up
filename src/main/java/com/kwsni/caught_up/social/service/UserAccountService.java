package com.kwsni.caught_up.social.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kwsni.caught_up.social.controller.dto.ChangePasswordDto;
import com.kwsni.caught_up.social.controller.dto.UserRegistrationDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.UserAccount;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.UserAccountRepository;

@Service
public class UserAccountService implements UserDetailsService {
    private final MemberRepository memberRepo;
    private final UserAccountRepository usrAccRepo;
    private final PasswordEncoder pwdEncoder;

    public UserAccountService(
        MemberRepository memberRepo,
        UserAccountRepository usrAccRepo,
        PasswordEncoder pwdEncoder
    ) {
        this.memberRepo = memberRepo;
        this.usrAccRepo = usrAccRepo;
        this.pwdEncoder = pwdEncoder;
    }

    public UserAccount loadUserAccountByUsername(String username) {
        var usrAcc = usrAccRepo.findByUsername(username);

        if(usrAcc == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return usrAcc;
    }

    public UserAccount loadUserAccountByEmail(String email) {
        var usrAcc = usrAccRepo.findByEmail(email);

        if(usrAcc == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return usrAcc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return loadUserAccountByUsername(username);
    }

    public void createUser(UserRegistrationDto registerDto) {
        var existingMember = memberRepo.findByUsername(registerDto.username());
        var existingEmail = memberRepo.findByEmail(registerDto.email());

        if(existingMember != null || existingEmail != null ) {
            throw new UserAlreadyExistsException();
        }

        if(!registerDto.password().equals(registerDto.confirmPassword())) {
            throw new PasswordNotConfirmedException();
        }

        String encodedPwd = pwdEncoder.encode(registerDto.password());

        var newMember = new Member(
            registerDto.email(),
            registerDto.username(),
            encodedPwd,
            "user");
        memberRepo.save(newMember);
    }

    public void updatePassword(
        String username,
        ChangePasswordDto changePwdDto
    ) throws BadCredentialsException {
        if(!changePwdDto.newPassword().equals(changePwdDto.confirmPassword())) {
            throw new PasswordNotConfirmedException();
        }

        var usrAcc = loadUserAccountByUsername(username);
        
        if(!pwdEncoder.matches(changePwdDto.currentPassword(), usrAcc.getPassword())) {
            throw new BadCredentialsException("Unable to authenticate user with given credentials");
        }

        var encodedPassword = pwdEncoder.encode(changePwdDto.newPassword());

        usrAcc.setPassword(encodedPassword);

        usrAccRepo.save(usrAcc);
    }

    public UserRegistrationDto createRegistrationForm() {
        return new UserRegistrationDto(
            "",
            "",
            "",
            ""
        );
    }

    public ChangePasswordDto createPasswordForm() {
        return new ChangePasswordDto(
            null,
            null,
            null
        );
    }

    public class BadUserInputException extends RuntimeException {
        public BadUserInputException(String msg) {
            super(msg);
        }
    }

    public class UserAlreadyExistsException extends BadUserInputException {
        public UserAlreadyExistsException() {
            super("User with given username or email already exists");
        }
    }

    public class PasswordNotConfirmedException extends BadUserInputException {
        public PasswordNotConfirmedException() {
            super("Passwords do not match");
        }
    }
}
