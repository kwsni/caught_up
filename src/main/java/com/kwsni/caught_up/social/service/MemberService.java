package com.kwsni.caught_up.social.service;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.EagerTransformation;
import com.cloudinary.utils.ObjectUtils;
import com.kwsni.caught_up.social.controller.dto.ChangePasswordDto;
import com.kwsni.caught_up.social.controller.dto.UserProfileDto;
import com.kwsni.caught_up.social.controller.dto.UserRegistrationDto;
import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.social.model.MemberFollow;
import com.kwsni.caught_up.social.model.MemberFollowKey;
import com.kwsni.caught_up.social.model.Review;
import com.kwsni.caught_up.social.model.UserAccount;
import com.kwsni.caught_up.social.repository.MemberFollowRepository;
import com.kwsni.caught_up.social.repository.MemberRepository;
import com.kwsni.caught_up.social.service.dto.MemberListDto;
import com.kwsni.caught_up.social.service.dto.MemberProfileDto;
import com.kwsni.caught_up.social.service.dto.MemberReviewsDto;
import com.kwsni.caught_up.tvdb.service.CloudinaryService;

@Service
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepo;
    private final MemberFollowRepository memberFollowRepo;
    private final ReviewService reviewSvc;
    private final CloudinaryService cloudinarySvc;
    private final PasswordEncoder pwdEncoder;

    public MemberService(
        MemberRepository memberRepo,
        MemberFollowRepository memberFollowRepo,
        ReviewService reviewSvc,
        CloudinaryService cloudinarySvc,
        PasswordEncoder pwdEncoder
    ) {
        this.memberRepo = memberRepo;
        this.memberFollowRepo = memberFollowRepo;
        this.reviewSvc = reviewSvc;
        this.cloudinarySvc = cloudinarySvc;
        this.pwdEncoder = pwdEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = memberRepo.findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserAccount(user);
    }

    public Member getMemberByUsername(String username) {
        var member = memberRepo.findByUsername(username);

        if(member == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return member;
    }

    public Member getMemberByEmail(String email) {
        var member = memberRepo.findByUsername(email);

        if(member == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return member;
    }

    public void createUser(UserRegistrationDto registerDto, boolean isGenerated) {
        var existingMember = memberRepo.findByUsername(registerDto.username());
        var existingEmail = memberRepo.findByEmail(registerDto.email());

        if(existingMember != null || existingEmail != null ) {
            throw new UserAlreadyExistsException();
        }

        if(!registerDto.password().equals(registerDto.confirmPassword())) {
            throw new PasswordNotConfirmedException();
        }

        String encodedPwd = pwdEncoder.encode(registerDto.password());

        var newMember = new Member(
            registerDto.email(),
            registerDto.username(),
            encodedPwd,
            "user",
            isGenerated
        );
        memberRepo.saveAndFlush(newMember);
    }

    public void updatePassword(
        String username,
        ChangePasswordDto changePwdDto
    ) throws BadCredentialsException {
        if(!changePwdDto.newPassword().equals(changePwdDto.confirmPassword())) {
            throw new PasswordNotConfirmedException();
        }

        var member = getMemberByUsername(username);
        
        if(!pwdEncoder.matches(changePwdDto.currentPassword(), member.getPassword())) {
            throw new BadCredentialsException("Unable to authenticate user with given credentials");
        }

        var encodedPassword = pwdEncoder.encode(changePwdDto.newPassword());

        member.setPassword(encodedPassword);

        memberRepo.save(member);
    }

    public UserRegistrationDto createRegistrationForm() {
        return new UserRegistrationDto(
            "",
            "",
            "",
            ""
        );
    }

    public ChangePasswordDto createPasswordForm() {
        return new ChangePasswordDto(
            null,
            null,
            null
        );
    }

    public List<Member> sampleGeneratedMembers(int rowLimit, int reviewLimit) {
        return memberRepo.sampleByGenerated(Limit.of(rowLimit), reviewLimit);
    }

    public UserProfileDto getProfile(String username) {
        var profileMember = getMemberByUsername(username);

        return new UserProfileDto(
            profileMember.getFirstName(),
            profileMember.getLastName(),
            profileMember.getBio(),
            profileMember.getLocation(),
            profileMember.getWebsite(),
            profileMember.getPronoun()
        );
    }
    
    public void modifyProfile(UserProfileDto profileDto, String username) {
        var profileMember = getMemberByUsername(username);

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

    public void changeAvatar(
        MultipartFile avatarFile,
        String username
    ) {
        var profileMember = getMemberByUsername(username);

        String avatarVersion = cloudinarySvc.uploadImage(
            avatarFile,
            username,
            ObjectUtils.asMap(
                "folder", "/avatars/",
                "public_id", username,
                "overwrite", true,
                "invalidate", true,
                "eager", Arrays.asList(
                    new EagerTransformation().named("avatar-SM"),
                    new EagerTransformation().named("avatar-MD"),
                    new EagerTransformation().named("avatar-LG")
                        
                )
            )
        );

        profileMember.setAvatar(avatarVersion);

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

        principalMember = principal != null ? getMemberByUsername(principal.getName()) : null;

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

    public boolean isFollowing(String followerUsername, String targetUsername) {
        Member followerMember = memberRepo.findByUsername(followerUsername);
        Member target = memberRepo.findByUsername(targetUsername);

        MemberFollow mf = new MemberFollow(followerMember, target);

        return followerMember.getMembersFollowed().contains(mf);
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

    public class BadUserInputException extends RuntimeException {
        public BadUserInputException(String msg) {
            super(msg);
        }
    }

    public class UserAlreadyExistsException extends BadUserInputException {
        public UserAlreadyExistsException() {
            super("User with given username or email already exists");
        }
    }

    public class PasswordNotConfirmedException extends BadUserInputException {
        public PasswordNotConfirmedException() {
            super("Passwords do not match");
        }
    }
}
