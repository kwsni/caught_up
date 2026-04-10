package com.kwsni.caught_up.social.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.social.model.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    ReviewComment getById(Long id);
}
