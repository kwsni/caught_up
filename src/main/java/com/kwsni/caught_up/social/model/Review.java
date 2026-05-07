package com.kwsni.caught_up.social.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.kwsni.caught_up.tvdb.model.Series;

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
import jakarta.persistence.OneToMany;

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

    @ManyToOne
    @JoinColumn(
        name = "series_tvdb_id",
        referencedColumnName = "tvdbId"
    )
    private Series series;

    @Column(columnDefinition = "TEXT")
    private String content;

    private OffsetDateTime watchedDate;

    private Double rating;

    private boolean isSpoiler;

    private boolean liked;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "review_like",
        joinColumns = @JoinColumn(name = "review_id"),
        inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> likes;
    
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    private List<ReviewComment> comments;
    
    @CreationTimestamp
    private OffsetDateTime createdDate;

    @Column(columnDefinition = "boolean default false")
    private boolean isGenerated;

    protected Review() {}
    
    public Review(Member author, Series series, String content, OffsetDateTime watchedDate, Double rating, boolean isSpoiler, boolean liked, boolean isGenerated) {
        this.author = author;
        this.series = series;
        this.content = content;
        this.watchedDate = watchedDate;
        this.rating = rating;
        this.isSpoiler = isSpoiler;
        this.liked = liked;
        this.isGenerated = isGenerated;
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
    
    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
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

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public OffsetDateTime getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(OffsetDateTime watchedDate) {
        this.watchedDate = watchedDate;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<ReviewComment> getComments() {
        return comments;
    }

    public void setComments(List<ReviewComment> comments) {
        this.comments = comments;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean isGenerated) {
        this.isGenerated = isGenerated;
    }
    
}
