package com.kwsni.caught_up.social.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import com.kwsni.caught_up.social.dto.PostCommentDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.model.ReviewComment;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.ReviewCommentRepository;
import com.kwsni.caught_up.social.repository.ReviewRepository;


@Controller
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewCommentRepository reviewCommentRepository;
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public String listReviews(
        @RequestParam(defaultValue="createdDate,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        
        Pageable sortBy = PageRequest.of(page, 18, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));
        Page<Review> reviewList = reviewRepository.findAll(sortBy);

        model.addAttribute("reviews", reviewList);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasPrev", reviewList.hasPrevious());
        model.addAttribute("hasNext", reviewList.hasNext());

        return "review-list";
    }
    
    @GetMapping("/post")
    public String showPostReview(WebRequest request, Model model) {

        return "post-review";
    }

    @GetMapping("/{reviewId}")
    public String showReview(@PathVariable("reviewId") Long reviewId, Model model, @AuthenticationPrincipal Member principal) {
        Review review = reviewRepository.findById(reviewId).get();

        boolean contained = review.getLikes().contains(principal);

        model.addAttribute("review", review);
        model.addAttribute("contained", contained);
        model.addAttribute("prin", principal);
        model.addAttribute("postComment", new PostCommentDto(""));
        

        return "review";
    }

    @PostMapping("/{reviewId}")
    public String postMethodName(@PathVariable("reviewId") Long reviewId,
        @ModelAttribute PostCommentDto postComment,
        Principal principal,
        Model model
    ) {
        Review review = reviewRepository.findById(reviewId).get();
        Member author = memberRepository.findByUsername(principal.getName());

        ReviewComment newReviewComment = new ReviewComment(author, review, postComment.content());

        reviewCommentRepository.save(newReviewComment);
        
        return "redirect:/reviews/" + review.getId();
    }
    
    
    @PostMapping("/{reviewId}/like")
    public String likeReview(@PathVariable("reviewId") Long reviewId,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal) {
            Review review = reviewRepository.findById(reviewId).get();
            Member member = memberRepository.findByUsername(principal.getName());

            review.getLikes().add(member);
            reviewRepository.save(review);

            return "redirect:" + referrer;
    }

    @PostMapping("/{reviewId}/unlike")
    public String unlikeReview(
        @PathVariable("reviewId") Long reviewId,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal
    ) {
            Review review = reviewRepository.findById(reviewId).get();
            Member member = memberRepository.findByUsername(principal.getName());

            review.getLikes().remove(member);
            reviewRepository.save(review);

            return "redirect:" + referrer;
    }
    
}
