package com.kwsni.caught_up.social.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member author;

    @Lob
    private String content;

    private boolean isSpoiler;
    
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
}
