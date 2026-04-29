package com.kwsni.caught_up.social.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kwsni.caught_up.social.controller.dto.ReviewRatingDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;


public interface ReviewRepository extends JpaRepository<Review, Long>{
    Page<Review> findByAuthor(Member author, Pageable pageable);
    Page<Review> findByAuthor_Username(String username, Pageable pageable);
    Page<Review> findByAuthor_UsernameAndRating(String username, Double rating, Pageable pageable);
    Page<Review> findByAuthor_FirstName(String firstName, Pageable pageable);
    Page<Review> findByAuthor_LastName(String lastName, Pageable pageable);
    Page<Review> findBySeries_Slug(String slug, Pageable pageable);
    Page<Review> findBySeries_SlugAndRating(String slug, Double rating, Pageable pageable);
    long countByAuthor_Username(String username);
    List<ReviewRatingDto> findBySeries_Slug(String slug);
    @Query("SELECT AVG(review.rating) FROM Review review WHERE review.series.slug = ?1")
    Double avgRatingsBySeries_Slug(String slug);
}
