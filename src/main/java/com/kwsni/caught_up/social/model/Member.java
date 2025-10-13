package com.kwsni.caught_up.social.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Member extends UserAccount {
    private String firstName;
    private String lastName;
    private String bio;
    private String location;
    private String website;
    @Enumerated(EnumType.STRING)
    private Pronoun pronoun;

    @ManyToMany(mappedBy = "likes", fetch = FetchType.LAZY)
    private Set<Review> likedReviews;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private Set<MemberFollow> membersFollowed;

    @OneToMany(mappedBy = "followed", fetch = FetchType.LAZY)
    private Set<MemberFollow> followingMembers;

    public Member() {
        super();
    }

    public Member(String email, String firstName, String lastName, String username, String password, String role) {
        super(email, username, password, role);

        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getBio() {
        return bio;
    }


    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getWebsite() {
        return website;
    }


    public void setWebsite(String website) {
        this.website = website;
    }


    public Pronoun getPronoun() {
        return pronoun;
    }


    public void setPronoun(Pronoun pronoun) {
        this.pronoun = pronoun;
    }

    public Set<Review> getLikedReviews() {
        return likedReviews;
    }

    public void setLikedReviews(Set<Review> likedReviews) {
        this.likedReviews = likedReviews;
    }

    public Set<MemberFollow> getMembersFollowed() {
        return membersFollowed;
    }

    public void setMembersFollowed(Set<MemberFollow> membersFollowed) {
        this.membersFollowed = membersFollowed;
    }

    public Set<MemberFollow> getFollowingMembers() {
        return followingMembers;
    }

    public void setFollowingMembers(Set<MemberFollow> followingMembers) {
        this.followingMembers = followingMembers;
    }
    
}
