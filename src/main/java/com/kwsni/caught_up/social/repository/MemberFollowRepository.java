package com.kwsni.caught_up.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.MemberFollow;
import com.kwsni.caught_up.social.model.MemberFollowKey;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, MemberFollowKey> {
    long countByFollower(Member member);
    long countByFollowed(Member member);
}
