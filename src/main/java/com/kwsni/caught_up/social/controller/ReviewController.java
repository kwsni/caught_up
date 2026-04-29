package com.kwsni.caught_up.social.controller;

import java.security.Principal;

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

import com.kwsni.caught_up.social.controller.dto.PostCommentDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.service.MemberService;
import com.kwsni.caught_up.social.service.ReviewService;


@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewSvc;
    private final MemberService memberSvc;

    public ReviewController(
        ReviewService reviewSvc,
        MemberService memberSvc
    ) {
        this.reviewSvc = reviewSvc;
        this.memberSvc = memberSvc;
    }

    @GetMapping
    public String listReviews(
        @RequestParam(defaultValue="createdDate,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        var reviewList = reviewSvc.getReviewList(sort, page);

        model.addAttribute("reviews", reviewList.getContent());
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
    public String showReview(
        @PathVariable("reviewId") Long reviewId,
        Model model,
        @AuthenticationPrincipal Member principal
    ) {
        var review = reviewSvc.getReview(reviewId);
        boolean contained = review.getLikes().contains(principal);

        model.addAttribute("review", review);
        model.addAttribute("contained", contained);
        model.addAttribute("prin", principal);
        model.addAttribute("postComment", new PostCommentDto(""));
        

        return "review";
    }

    @PostMapping("/{reviewId}")
    public String postReviewComment(
        @PathVariable("reviewId") Long reviewId,
        @ModelAttribute PostCommentDto postComment,
        Principal principal,
        Model model
    ) {
        var author = memberSvc.getMember(principal.getName());

        reviewSvc.saveReviewComment(reviewId, postComment, author);
        return "redirect:/reviews/" + reviewId;
    }
    
    
    @PostMapping("/{reviewId}/like")
    public String postLikeReview(
        @PathVariable("reviewId") Long reviewId,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal
    ) {
        var member = memberSvc.getMember(principal.getName());

        reviewSvc.likeReview(reviewId, member);    
        return "redirect:" + referrer;
    }

    @PostMapping("/{reviewId}/unlike")
    public String postUnlikeReview(
        @PathVariable("reviewId") Long reviewId,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal
    ) {
        var member = memberSvc.getMember(principal.getName());
        
        reviewSvc.unlikeReview(reviewId, member);
        return "redirect:" + referrer;
    }
    
}
