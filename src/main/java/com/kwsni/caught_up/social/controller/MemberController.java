package com.kwsni.caught_up.social.controller;

import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kwsni.caught_up.social.service.MemberService;

@Controller
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberSvc;

    public MemberController(MemberService memberSvc) {
        this.memberSvc = memberSvc;
    }

    @GetMapping
    public String listMembers(
        @RequestParam(defaultValue="reviewCount,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Principal principal,
        Model model
    ) {
        var memberListDto = memberSvc.getMemberList(sort, page, principal);
        
        model.addAttribute("principalMember", memberListDto.principalMember());
        model.addAttribute("members", memberListDto.memberList());
        model.addAttribute("hasPrev", memberListDto.hasPrev());
        model.addAttribute("hasNext", memberListDto.hasNext());
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        return "member-list";
    }

    @GetMapping("/{memberUsername}")
    public String showMemberProfile(
        @PathVariable("memberUsername") String memberUsername,
        Principal principal,
        Model model
    ) {
        var memberProfileDto = memberSvc.getMemberProfile(memberUsername, principal, model);

        if(memberProfileDto.isFollowing().isPresent()) {
            model.addAttribute("isFollowing", memberProfileDto.isFollowing().get().booleanValue());
        }
        if(memberProfileDto.isFollowed().isPresent()) {
            model.addAttribute("isFollowed", memberProfileDto.isFollowed().get().booleanValue());
        }
        model.addAttribute("member", memberProfileDto.member());
        model.addAttribute("recentReviews", memberProfileDto.recentReviews());
        model.addAttribute("popularReviews", memberProfileDto.popularReviews());
        return "profile";
    }

    @GetMapping("/{memberUsername}/reviews")
    public String showMemberReviews(
        @PathVariable("memberUsername") String memberUsername,
        @RequestParam(required=false) Double rating, 
        @RequestParam(defaultValue="createdDate,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        var memberReviewsDto = memberSvc.getMemberReviews(memberUsername, rating, sort, page);

        model.addAttribute("member", memberReviewsDto.member());
        model.addAttribute("reviews", memberReviewsDto.reviewsList());
        model.addAttribute("isBrowse", true);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasPrev", memberReviewsDto.hasPrev());
        model.addAttribute("hasNext", memberReviewsDto.hasNext());

        return "member-reviews";
    }
    
    @PostMapping("/{memberUsername}/follow")
    public String postFollowMember(@PathVariable("memberUsername") String memberUsername,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal
    ) {
        memberSvc.followMember(memberUsername, principal);
        return "redirect:" + referrer;
    }
    
    @PostMapping("/{memberUsername}/unfollow")
    public String postUnfollowMember(@PathVariable("memberUsername") String memberUsername,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal
    ) {
        memberSvc.unfollowMember(memberUsername, principal); 
        return "redirect:" + referrer;
    }
}
