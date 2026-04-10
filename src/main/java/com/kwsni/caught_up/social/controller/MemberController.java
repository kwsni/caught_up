package com.kwsni.caught_up.social.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.MemberFollow;
import com.kwsni.caught_up.social.model.MemberFollowKey;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.repository.MemberFollowRepository;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.repository.ReviewRepository;

@Controller
@RequestMapping("/members")
public class MemberController {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberFollowRepository memberFollowRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping
    public String listMembers(
        @RequestParam(defaultValue="reviewCount,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Principal principal,
        Model model
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        
        Pageable sortBy = PageRequest.of(page, 18, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));;
        List<Member> memberList;
        
        if(sortField.equals("reviewCount")) {
            Page<Object[]> memberPage = memberRepository.findAllOrderByReviewCount(sortBy);
            memberList = memberPage.getContent().stream().map(result -> (Member) result[0]).toList();
            model.addAttribute("hasPrev", memberPage.hasPrevious());
            model.addAttribute("hasNext", memberPage.hasNext());
        } else if(sortField.equals("followerCount")) {
            Page<Object[]> memberPage = memberRepository.findAllOrderByFollowerCount(sortBy);
            memberList = memberPage.getContent().stream().map(result -> (Member) result[0]).toList();
            model.addAttribute("hasPrev", memberPage.hasPrevious());
            model.addAttribute("hasNext", memberPage.hasNext());
        } else {
            Page<Member> memberPage = memberRepository.findAll(sortBy);
            memberList = memberPage.getContent();
            model.addAttribute("hasPrev", memberPage.hasPrevious());
            model.addAttribute("hasNext", memberPage.hasNext());
        }

        if(principal != null) {
            Member principalMember = memberRepository.findByUsername(principal.getName());
            model.addAttribute("principalMember", principalMember);
        }
        
        model.addAttribute("members", memberList);
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
        Member member = memberRepository.findByUsername(memberUsername);
        if(principal != null) {
            Member principalMember = memberRepository.findByUsername(principal.getName());

            MemberFollow principalFollowMember = new MemberFollow(principalMember, member);
            MemberFollow memberFollowPrincipal = new MemberFollow(member, principalMember);

            boolean isFollowing = principalMember.getMembersFollowed().contains(principalFollowMember);
            boolean isFollowed = member.getMembersFollowed().contains(memberFollowPrincipal);

            model.addAttribute("isFollowing", isFollowing);
            model.addAttribute("isFollowed", isFollowed);
        }

        Pageable sortedByDateDesc = PageRequest.of(0, 12, Sort.by("createdDate").descending());
        Page<Review> recentReviews = reviewRepository.findByAuthor(member, sortedByDateDesc);

        Pageable sortedByPopularityDesc = PageRequest.of(0, 12, Sort.by("id").descending());
        Page<Review> popularReviews = reviewRepository.findByAuthor(member, sortedByPopularityDesc);

        model.addAttribute("member", member);
        model.addAttribute("recentReviews", recentReviews);
        model.addAttribute("popularReviews", popularReviews);

        return "profile";
    }

    @GetMapping("/{memberUsername}/reviews")
    public String getMemberReviews(
        @PathVariable("memberUsername") String memberUsername,
        @RequestParam(required=false) Double rating, 
        @RequestParam(defaultValue="createdDate,desc") String[] sort,
        @RequestParam(defaultValue="0") int page,
        Model model
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;

        Pageable sortBy = PageRequest.of(page, 12, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));
        Page<Review> reviewsList;
        if(rating != null) {
            reviewsList = reviewRepository.findByAuthor_UsernameAndRating(memberUsername, rating, sortBy);
        } else {
            reviewsList = reviewRepository.findByAuthor_Username(memberUsername, sortBy);
        }

        Member member = memberRepository.findByUsername(memberUsername);


        model.addAttribute("member", member);
        model.addAttribute("reviews", reviewsList);
        model.addAttribute("isBrowse", true);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasPrev", reviewsList.hasPrevious());
        model.addAttribute("hasNext", reviewsList.hasNext());

        return "member-reviews";
    }
    
    @PostMapping("/{memberUsername}/follow")
    public String followMember(@PathVariable("memberUsername") String memberUsername,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal
    ) {
        Member member = memberRepository.findByUsername(memberUsername);
        Member principalMember = memberRepository.findByUsername(principal.getName());

        MemberFollow mf = new MemberFollow(principalMember, member);

        memberFollowRepository.save(mf);

        return "redirect:" + referrer;
    }
    
    @PostMapping("/{memberUsername}/unfollow")
    public String unfollowMember(@PathVariable("memberUsername") String memberUsername,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal
    ) {
        Member member = memberRepository.findByUsername(memberUsername);
        Member principalMember = memberRepository.findByUsername(principal.getName());

        MemberFollowKey memberFollowKey = new MemberFollowKey(principalMember.getId(), member.getId());

        memberFollowRepository.deleteById(memberFollowKey);
        
        return "redirect:" + referrer;
    }
}
