package com.kwsni.caught_up.social.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kwsni.caught_up.social.model.UserAccount;
import com.kwsni.caught_up.social.repository.UserAccountRepository;

public class UserAccountService implements UserDetailsService {
    
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username);

        if(userAccount == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userAccount;
    }
}
