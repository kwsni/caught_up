package com.kwsni.caught_up.social.service.dto;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;

public record MemberProfileDto(
    Optional<Boolean> isFollowing,
    Optional<Boolean> isFollowed,
    Member member,
    Page<Review> recentReviews,
    Page<Review> popularReviews
) {
    
}
