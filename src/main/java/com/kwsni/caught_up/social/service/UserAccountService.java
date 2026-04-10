package com.kwsni.caught_up.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.UserAccount;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.UserAccountRepository;

@Service
public class UserAccountService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserAccount loadUserAccountByUsername(String username) {
        UserAccount userAccount = userAccountRepository.findByUsername(username);

        if(userAccount == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userAccount;
    }

    public UserAccount loadUserAccountByEmail(String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email);

        if(userAccount == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userAccount;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return loadUserAccountByUsername(username);
    }

    public void createUser(String username, String email, String password, String confirmPassword) {
        Member existingMember = memberRepository.findByUsername(username);
        Member existingEmail = memberRepository.findByEmail(email);

        if(existingMember != null || existingEmail != null ) {
            throw new UserAlreadyExistsException();
        }

        if(!password.equals(confirmPassword)) {
            throw new PasswordNotConfirmedException();
        }

        String encodedPassword = passwordEncoder.encode(password);

        Member newMember = new Member(
            email,
            username,
            encodedPassword,
            "user");
        memberRepository.save(newMember);
    }

    public void updatePassword(String username, String currentPassword, String newPassword, String confirmPassword) throws BadCredentialsException {
        if(!newPassword.equals(confirmPassword)) {
            throw new PasswordNotConfirmedException();
        }

        UserAccount userAccount = loadUserAccountByUsername(username);
        
        if(!passwordEncoder.matches(currentPassword, userAccount.getPassword())) {
            throw new BadCredentialsException("Unable to authenticate user with given credentials");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        userAccount.setPassword(encodedPassword);

        userAccountRepository.save(userAccount);
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
