package com.kwsni.caught_up.social.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;


public interface ReviewRepository extends JpaRepository<Review, Long>{
    Page<Review> findByAuthor(Member author, Pageable pageable);
    Page<Review> findByAuthor_Username(String username, Pageable pageable);
    Page<Review> findByAuthor_FirstName(String firstName, Pageable pageable);
    Page<Review> findByAuthor_LastName(String lastName, Pageable pageable);
}
