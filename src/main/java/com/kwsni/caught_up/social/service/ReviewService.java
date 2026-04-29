package com.kwsni.caught_up.social.service;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.kwsni.caught_up.social.controller.dto.PostCommentDto;
import com.kwsni.caught_up.social.controller.dto.PostReviewDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.model.ReviewComment;
import com.kwsni.caught_up.social.repository.ReviewCommentRepository;
import com.kwsni.caught_up.social.repository.ReviewRepository;
import com.kwsni.caught_up.tvdb.model.Series;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final ReviewCommentRepository reviewCommentRepo;

    public ReviewService(
        ReviewRepository reviewRepo,
        ReviewCommentRepository reviewCommentRepo
    ) {
        this.reviewRepo = reviewRepo;
        this.reviewCommentRepo = reviewCommentRepo;
    }

    public Page<Review> getReviewList(String[] sort, int page) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        Pageable sortBy = PageRequest.of(page, 18, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));
        
        return reviewRepo.findAll(sortBy);
    }

    public Page<Review> getSeriesReviewList(
        String slug,
        Double rating,
        String[] sort,
        int page
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        Pageable sortBy = PageRequest.of(page, 12, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));

        if(rating != null) {
            return reviewRepo.findBySeries_SlugAndRating(slug, rating, sortBy);
        } else {
            return reviewRepo.findBySeries_Slug(slug, sortBy);
        }
    }

    public Page<Review> getMemberReviewList(
        String memberUsername,
        String[] sort,
        int page
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        Pageable sortBy = PageRequest.of(page, 12, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));

        return reviewRepo.findByAuthor_Username(memberUsername, sortBy);
    }

    public Page<Review> getMemberReviewList(
        String memberUsername,
        Double rating,
        String[] sort,
        int page
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        Pageable sortBy = PageRequest.of(page, 12, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));

        return reviewRepo.findByAuthor_UsernameAndRating(memberUsername, rating, sortBy);
    }

    public Review getReview(Long reviewId) {
        return reviewRepo.findById(reviewId).get();
    }

    public Page<Review> getRecentReviews() {
        Pageable sortedByDateDesc = PageRequest.of(0, 12, Sort.by("createdDate").descending());
        return reviewRepo.findAll(sortedByDateDesc);
    }

    public Page<Review> getRecentReviews(Member member) {
        Pageable sortedByDateDesc = PageRequest.of(0, 12, Sort.by("createdDate").descending());
        return reviewRepo.findByAuthor(member, sortedByDateDesc);
    }

    public Page<Review> getPopularReviews(String slug) {
        //TODO: SORT BY LIKES SIZE USING JPQL?
        return reviewRepo.findBySeries_Slug(slug, PageRequest.of(0, 3, Sort.by("id").descending()));
    }

    public Page<Review> getPopularReviews(Member member) {
        Pageable sortedByPopularityDesc = PageRequest.of(0, 12, Sort.by("id").descending());
        return reviewRepo.findByAuthor(member, sortedByPopularityDesc);
    }

    public Double avgRating(String slug) {
        return reviewRepo.avgRatingsBySeries_Slug(slug);
    }

    public long saveReview(
        Member author,
        Series series,
        PostReviewDto postReview,
        TimeZone timezone
    ) {
        ZoneId tz = timezone.toZoneId();
        var review = new Review(
            author,
            series,
            postReview.content(),
            postReview.watchedOn() != null ? postReview.watchedOn().atStartOfDay(tz).toOffsetDateTime() : null,
            postReview.rating(),
            postReview.isSpoiler(),
            postReview.like()
        );

        review = reviewRepo.save(review);
        return review.getId();
    }

    public void saveReviewComment(
        Long reviewId,
        PostCommentDto comment,
        Member author
    ) {
        Review review = reviewRepo.findById(reviewId).get();
        ReviewComment newReviewComment = new ReviewComment(author, review, comment.content());

        reviewCommentRepo.save(newReviewComment);
    }

    public void likeReview(Long reviewId, Member member) {
        Review review = reviewRepo.findById(reviewId).get();

        review.getLikes().add(member);
        reviewRepo.save(review);
    }

    public void unlikeReview(Long reviewId, Member member) {
        Review review = reviewRepo.findById(reviewId).get();

        review.getLikes().remove(member);
        reviewRepo.save(review);
    }
}
