package com.kwsni.caught_up.social.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.kwsni.caught_up.social.controller.dto.UserProfileDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.MemberFollow;
import com.kwsni.caught_up.social.model.MemberFollowKey;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.repository.MemberFollowRepository;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.service.dto.MemberListDto;
import com.kwsni.caught_up.social.service.dto.MemberProfileDto;
import com.kwsni.caught_up.social.service.dto.MemberReviewsDto;

@Service
public class MemberService {
    private final MemberRepository memberRepo;
    private final MemberFollowRepository memberFollowRepo;
    private final ReviewService reviewSvc;

    public MemberService(
        MemberRepository memberRepo,
        MemberFollowRepository memberFollowRepo,
        ReviewService reviewSvc
    ) {
        this.memberRepo = memberRepo;
        this.memberFollowRepo = memberFollowRepo;
        this.reviewSvc = reviewSvc;
    }

    public Member getMember(String username) {
        return memberRepo.findByUsername(username);
    }

    public UserProfileDto getProfile(String username) {
        var profileMember = getMember(username);

        return new UserProfileDto(
            profileMember.getAvatar(),
            profileMember.getFirstName(),
            profileMember.getLastName(),
            profileMember.getBio(),
            profileMember.getLocation(),
            profileMember.getWebsite(),
            profileMember.getPronoun()
        );
    }
    
    public void modifyProfile(UserProfileDto profileDto, String username) {
        var profileMember = getMember(username);

        profileMember.setAvatar(profileDto.avatar());
        profileMember.setFirstName(profileDto.firstName());
        profileMember.setLastName(profileDto.lastName());
        profileMember.setBio(profileDto.bio());
        profileMember.setLocation(profileDto.location());
        profileMember.setWebsite(profileDto.website()
            .replace("http://", "")
            .replace("https://", "")
        );

        memberRepo.save(profileMember);
    }

    public MemberListDto getMemberList(
        String[] sort,
        int page,
        Principal principal
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        
        Pageable sortBy = PageRequest.of(page, 18, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));

        MemberListDto memberListDto;
        List<Member> memberList;
        Member principalMember;

        principalMember = principal != null ? getMember(principal.getName()) : null;

        if(sortField.equals("reviewCount")) {
            Page<Object[]> memberPage = memberRepo.findAllOrderByReviewCount(sortBy);
            memberList = memberPage.getContent().stream().map(result -> (Member) result[0]).toList();
            memberListDto = new MemberListDto(
                principalMember,
                memberList,
                memberPage.hasPrevious(),
                memberPage.hasNext()
            );
        } else if(sortField.equals("followerCount")) {
            Page<Object[]> memberPage = memberRepo.findAllOrderByFollowerCount(sortBy);
            memberList = memberPage.getContent().stream().map(result -> (Member) result[0]).toList();
            memberListDto = new MemberListDto(
                principalMember,
                memberList,
                memberPage.hasPrevious(),
                memberPage.hasNext()
            );
        } else {
            Page<Member> memberPage = memberRepo.findAll(sortBy);
            memberList = memberPage.getContent();
            memberListDto = new MemberListDto(
                principalMember,
                memberList,
                memberPage.hasPrevious(),
                memberPage.hasNext()
            );
        }
        
        return memberListDto;
    }

    public MemberProfileDto getMemberProfile(
        String memberUsername,
        Principal principal,
        Model model
    ) {
        Member member = memberRepo.findByUsername(memberUsername);
        Optional<Boolean> isFollowing = Optional.empty();
        Optional<Boolean> isFollowed = Optional.empty();

        if(principal != null) {
            Member principalMember = memberRepo.findByUsername(principal.getName());

            MemberFollow principalFollowMember = new MemberFollow(principalMember, member);
            MemberFollow memberFollowPrincipal = new MemberFollow(member, principalMember);

            isFollowing = Optional.of(principalMember.getMembersFollowed().contains(principalFollowMember));
            isFollowed = Optional.of(member.getMembersFollowed().contains(memberFollowPrincipal));
        }

        var recentReviews = reviewSvc.getRecentReviews(member);
        var popularReviews = reviewSvc.getPopularReviews(member);
        
        return new MemberProfileDto(
            isFollowing,
            isFollowed,
            member,
            recentReviews,
            popularReviews
        );
    }

    public MemberReviewsDto getMemberReviews(
        String memberUsername,
        Double rating,
        String[] sort,
        int page
    ) {
        Page<Review> reviewsList;
        if(rating != null) {
            reviewsList = reviewSvc.getMemberReviewList(memberUsername, rating, sort, page);
        } else {
            reviewsList = reviewSvc.getMemberReviewList(memberUsername, sort, page);
        }

        Member member = memberRepo.findByUsername(memberUsername);

        return new MemberReviewsDto(
            member,
            reviewsList,
            reviewsList.hasPrevious(),
            reviewsList.hasNext()
        );
    }

    public void followMember(
        String memberUsername,
        Principal principal
    ) {
        Member member = memberRepo.findByUsername(memberUsername);
        Member principalMember = memberRepo.findByUsername(principal.getName());

        MemberFollow mf = new MemberFollow(principalMember, member);

        memberFollowRepo.save(mf);
    }

    public void unfollowMember(
        String memberUsername,
        Principal principal
    ) {
        Member member = memberRepo.findByUsername(memberUsername);
        Member principalMember = memberRepo.findByUsername(principal.getName());

        MemberFollowKey memberFollowKey = new MemberFollowKey(principalMember.getId(), member.getId());

        memberFollowRepo.deleteById(memberFollowKey);   
    }
}
