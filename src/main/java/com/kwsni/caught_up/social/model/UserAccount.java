package com.kwsni.caught_up.social.model;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAccount implements UserDetails, CredentialsContainer {
    private final Member user;

    public UserAccount(Member user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return Arrays.asList(authority);
    }

    @Override
    public void eraseCredentials() {
        user.setPassword(null);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UserAccount u) {
            return user.getUsername().equals(u.getUsername());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return user.getUsername().hashCode();
    }
    
    public Member getUser() {
        return this.user;
    }
}
