package com.kwsni.caught_up.tvdb.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Integer tvdbId;

    private String name;
    
    private String year;

    private String firstAired;

    private String lastAired;

    private String nextAired;
    
    private Double score;
    
    private String image;
    
    @OneToMany
    private List<Episode> episodes;
    
    private String country;
    
    private String lastUpdated;

    private String slug;

    protected Series() {};

    public Series(Integer tvdbId, String name, String year, String firstAired, String lastAired, String nextAired,
            Double score, String image, List<Episode> episodes, String country, String lastUpdated, String slug) {
        this.tvdbId = tvdbId;
        this.name = name;
        this.year = year;
        this.firstAired = firstAired;
        this.lastAired = lastAired;
        this.nextAired = nextAired;
        this.score = score;
        this.image = image;
        this.episodes = episodes;
        this.country = country;
        this.lastUpdated = lastUpdated;
        this.slug = slug;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTvdbId() {
        return tvdbId;
    }

    public void setTvdbId(Integer tvdbId) {
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

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
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

}
