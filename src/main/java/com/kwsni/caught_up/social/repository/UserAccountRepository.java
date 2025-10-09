package com.kwsni.caught_up.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.social.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByUsername(String username);
    UserAccount findByEmail(String email);
}
