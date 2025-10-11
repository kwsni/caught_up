package com.kwsni.caught_up.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.social.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsername(String username);
}
