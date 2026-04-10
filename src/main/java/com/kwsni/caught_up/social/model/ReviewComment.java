package com.kwsni.caught_up.social.model;

import java.time.OffsetDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class ReviewComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(
        name = "member_id",
        referencedColumnName = "id"
    )
    private Member author;

    @ManyToOne
    @JoinColumn(
        name = "review_id",
        referencedColumnName = "id"
    )
    private Review review;

    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "review_comment_like",
        joinColumns = @JoinColumn(name = "review_comment_id"),
        inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> likes;
    
    @CreationTimestamp
    private OffsetDateTime createdDate;

    protected ReviewComment() {}
    
    public ReviewComment(Member author, Review review, String content) {
        this.author = author;
        this.review = review;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Member getAuthor() {
        return author;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Member> getLikes() {
        return likes;
    }

    public void setLikes(Set<Member> likes) {
        this.likes = likes;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
