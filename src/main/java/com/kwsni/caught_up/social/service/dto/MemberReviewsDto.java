package com.kwsni.caught_up.social.service.dto;

import org.springframework.data.domain.Page;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;

public record MemberReviewsDto(
    Member member,
    Page<Review> reviewsList,
    boolean hasPrev,
    boolean hasNext
) {
    
}
