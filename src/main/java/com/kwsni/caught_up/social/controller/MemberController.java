package com.kwsni.caught_up.social.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.MemberFollow;
import com.kwsni.caught_up.social.model.MemberFollowKey;
import com.kwsni.caught_up.social.repository.MemberFollowRepository;
import com.kwsni.caught_up.social.repository.MemberRepository;

@Controller
@RequestMapping("/members")
public class MemberController {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberFollowRepository memberFollowRepository;

    @GetMapping("/{memberUsername}")
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
    
    @PostMapping("/{memberUsername}/follow")
    public String followMember(@PathVariable("memberUsername") String memberUsername,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
        Principal principal) {
            Member member = memberRepository.findByUsername(memberUsername);
            Member principalMember = memberRepository.findByUsername(principal.getName());

            MemberFollow mf = new MemberFollow(principalMember, member);

            memberFollowRepository.save(mf);

            return "redirect:" + referrer;
    }
    
    @PostMapping("/{memberUsername}/unfollow")
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
