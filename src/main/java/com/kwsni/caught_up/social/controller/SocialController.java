package com.kwsni.caught_up.social.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.WebRequest;

import com.kwsni.caught_up.social.dto.ReviewDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.MemberFollow;
import com.kwsni.caught_up.social.model.MemberFollowKey;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.repository.MemberFollowRepository;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.ReviewRepository;

import jakarta.servlet.http.HttpServletRequest;






@Controller
public class SocialController {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberFollowRepository memberFollowRepository;

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

    @GetMapping("/reviews/{reviewId}")
    public String showReview(@PathVariable("reviewId") Long reviewId, Model model) {
        // handle exception
        Review review = reviewRepository.findById(reviewId).get();

        model.addAttribute("review", review);

        return "review";
    }
    
    @PostMapping("/reviews/{reviewId}/like")
    public String likeReview(@PathVariable("reviewId") Long reviewId,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal) {
            Review review = reviewRepository.findById(reviewId).get();
            Member member = memberRepository.findByUsername(principal.getName());

            review.getLikes().add(member);
            reviewRepository.save(review);

            return "redirect:" + referrer;
    }

    @GetMapping("/user/{memberUsername}")
    public String showMemberProfile(@PathVariable("memberUsername") String memberUsername,
        Principal principal,
        Model model) {
            Member member = memberRepository.findByUsername(memberUsername);
            Member principalMember = memberRepository.findByUsername(principal.getName());

            MemberFollow principalFollowMember = new MemberFollow(principalMember, member);
            MemberFollow memberFollowPrincipal = new MemberFollow(member, principalMember);

            boolean isFollowing = principalMember.getMembersFollowed().contains(principalFollowMember);
            boolean isFollowed = member.getMembersFollowed().contains(memberFollowPrincipal);

            model.addAttribute("member", member);
            model.addAttribute("isFollowing", isFollowing);
            model.addAttribute("isFollowed", isFollowed);

            return "profile";
    }
    
    @PostMapping("/user/{memberUsername}/follow")
    public String followMember(@PathVariable("memberUsername") String memberUsername,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal) {
            Member member = memberRepository.findByUsername(memberUsername);
            Member principalMember = memberRepository.findByUsername(principal.getName());

            MemberFollow mf = new MemberFollow(principalMember, member);

            memberFollowRepository.save(mf);

            return "redirect:" + referrer;
    }
    
    @PostMapping("/user/{memberUsername}/unfollow")
    public String unfollowMember(@PathVariable("memberUsername") String memberUsername,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal) {
            Member member = memberRepository.findByUsername(memberUsername);
            Member principalMember = memberRepository.findByUsername(principal.getName());

            MemberFollowKey memberFollowKey = new MemberFollowKey(principalMember.getId(), member.getId());

            memberFollowRepository.deleteById(memberFollowKey);
            
            return "redirect:" + referrer;
    }
}
