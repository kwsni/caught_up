package com.kwsni.caught_up.social.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import com.kwsni.caught_up.social.dto.ReviewDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.ReviewRepository;

import jakarta.servlet.http.HttpServletRequest;



@Controller
public class SocialController {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/reviews")
    public String listReviews(Model model) {
        // Redirect to this sort (ie. mapping like '/reviews/{sort}/{order}')
        Pageable sortedByLatest = PageRequest.of(0, 18, Sort.by("createdDate").descending());
        Page<Review> pagedReviews = reviewRepository.findAll(sortedByLatest);
        model.addAttribute("reviews", pagedReviews);
        return "reviews";
    }
    
    @GetMapping("/post-review")
    public String showPostReview(WebRequest request, Model model) {
        model.addAttribute("review", new ReviewDto(null,"", false));
        return "post-review";
    }
    
    @PostMapping("/post-review")
    public String postReview(@ModelAttribute ReviewDto reviewDto,
        Principal principal,
        Errors errors,
        HttpServletRequest request,
        Model model) {
            Member author = memberRepository.findByUsername(principal.getName());
            Review newReview = new Review(
                author,
                reviewDto.content(),
                reviewDto.isSpoiler()
            );
            reviewRepository.save(newReview);
            return "redirect:/reviews";
    }
}
