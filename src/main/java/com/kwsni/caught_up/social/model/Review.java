package com.kwsni.caught_up.social.model;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    @JoinColumn(
        name = "member_id",
        referencedColumnName = "id"
    )
    private Member author;

    @Lob
    private String content;

    private boolean isSpoiler;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "review_like",
        joinColumns = @JoinColumn(name = "review_id"),
        inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> likes;
    
    @CreationTimestamp
    private LocalDateTime createdDate;

    protected Review() {}
    
    public Review(Member author, String content, boolean isSpoiler) {
        this.author = author;
        this.content = content;
        this.isSpoiler = isSpoiler;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSpoiler() {
        return isSpoiler;
    }

    public void setSpoiler(boolean isSpoiler) {
        this.isSpoiler = isSpoiler;
    }

    public Set<Member> getLikes() {
        return likes;
    }

    public void setLikes(Set<Member> likes) {
        this.likes = likes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
}
