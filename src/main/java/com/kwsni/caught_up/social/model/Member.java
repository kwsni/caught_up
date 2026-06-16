package com.kwsni.caught_up.social.model;

import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    private String password;
    
    private String role;

    @Column(columnDefinition = "boolean default false")
    private boolean isGenerated;

    private String firstName;

    private String lastName;

    @ColumnDefault("\'https://cdn.discordapp.com/embed/avatars/1.png\'")
    private String avatar;

    private String bio;

    private String location;

    private String website;

    @Enumerated(EnumType.STRING)
    private Pronoun pronoun;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("author")
    private Set<Review> reviews;

    @ManyToMany(mappedBy = "likes", fetch = FetchType.LAZY)
    private Set<Review> likedReviews;

    @OneToMany(mappedBy = "follower", fetch = FetchType.EAGER)
    private Set<MemberFollow> membersFollowed;

    @OneToMany(mappedBy = "followed", fetch = FetchType.EAGER)
    private Set<MemberFollow> followingMembers;

    protected Member() {}

    public Member(String email, String username, String password, String role, boolean isGenerated) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isGenerated = isGenerated;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Member u) {
            return this.username.equals(u.getUsername());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean isGenerated) {
        this.isGenerated = isGenerated;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
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
    
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }
}
