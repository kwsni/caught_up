package com.kwsni.caught_up.tvdb.model;

import java.util.List;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kwsni.caught_up.social.model.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Series extends TvdbEntity<Long>{
    @Id
    private Long tvdbId;

    private String name;
    
    private String year;

    private String firstAired;

    private String lastAired;

    private String nextAired;
    
    private Double score;
    
    private String image;

    @Column(columnDefinition = "TEXT")
    private String overview;
    
    @OneToMany(mappedBy = "series", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("series")
    private List<Episode> episodes;

    @OneToMany(mappedBy = "series", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews;
    
    // Likely need to cache
    @Formula(value = "(SELECT AVG(r.rating) FROM Review r WHERE r.series_tvdb_id = tvdb_id)")
    private Double avgRating;

    private String country;
    
    private String lastUpdated;

    private String slug;

    protected Series() {}

    public Series(Long tvdbId, String name, String year, String firstAired, String lastAired, String nextAired,
            Double score, String image, String overview, List<Episode> episodes, String country, String lastUpdated, String slug) {
        this.tvdbId = tvdbId;
        this.name = name;
        this.year = year;
        this.firstAired = firstAired;
        this.lastAired = lastAired;
        this.nextAired = nextAired;
        this.score = score;
        this.image = image;
        this.overview = overview;
        this.episodes = episodes;
        this.country = country;
        this.lastUpdated = lastUpdated;
        this.slug = slug;
    }

    public Series(Long tvdbId, String name, String year, String firstAired, String lastAired, String nextAired,
            Double score, String image, String overview, String country, String lastUpdated, String slug) {
        this.tvdbId = tvdbId;
        this.name = name;
        this.year = year;
        this.firstAired = firstAired;
        this.lastAired = lastAired;
        this.nextAired = nextAired;
        this.score = score;
        this.image = image;
        this.overview = overview;
        this.country = country;
        this.lastUpdated = lastUpdated;
        this.slug = slug;
    }

    public Long getTvdbId() {
        return tvdbId;
    }

    public Long getId() {
        return tvdbId;
    }

    public void setTvdbId(Long tvdbId) {
        this.tvdbId = tvdbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFirstAired() {
        return firstAired;
    }

    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    public String getLastAired() {
        return lastAired;
    }

    public void setLastAired(String lastAired) {
        this.lastAired = lastAired;
    }

    public String getNextAired() {
        return nextAired;
    }

    public void setNextAired(String nextAired) {
        this.nextAired = nextAired;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public void addEpisode(Episode episode) {
        this.episodes.add(episode);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

}
