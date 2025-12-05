package com.kwsni.caught_up.tvdb.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Long tvdbId;

    private String name;
    
    private Integer seasonNumber;

    private String seasonName;

    private Integer airsAfterSeason;

    private Integer airsBeforeEpisode;

    private Integer airsBeforeSeason;
    
    private Integer number;
    
    private Integer absoluteNumber;
    
    @Nullable
    private Integer runtime;
    
    private String aired;

    private String year;
    
    private String image;

    @Nullable
    private Integer imageType;
    
    @Lob
    private String overview;
    
    private Long isMovie;

    protected Episode() {}

    public Episode(Long tvdbId, String name, Integer seasonNumber, String seasonName, Integer airsAfterSeason,
            Integer airsBeforeEpisode, Integer airsBeforeSeason, Integer number, Integer absoluteNumber,
            Integer runtime, String aired, String year, String image, Integer imageType, String overview,
            Long isMovie) {
        this.tvdbId = tvdbId;
        this.name = name;
        this.seasonNumber = seasonNumber;
        this.seasonName = seasonName;
        this.airsAfterSeason = airsAfterSeason;
        this.airsBeforeEpisode = airsBeforeEpisode;
        this.airsBeforeSeason = airsBeforeSeason;
        this.number = number;
        this.absoluteNumber = absoluteNumber;
        this.runtime = runtime;
        this.aired = aired;
        this.year = year;
        this.image = image;
        this.imageType = imageType;
        this.overview = overview;
        this.isMovie = isMovie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTvdbId() {
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

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public Integer getAirsAfterSeason() {
        return airsAfterSeason;
    }

    public void setAirsAfterSeason(Integer airsAfterSeason) {
        this.airsAfterSeason = airsAfterSeason;
    }

    public Integer getAirsBeforeEpisode() {
        return airsBeforeEpisode;
    }

    public void setAirsBeforeEpisode(Integer airsBeforeEpisode) {
        this.airsBeforeEpisode = airsBeforeEpisode;
    }

    public Integer getAirsBeforeSeason() {
        return airsBeforeSeason;
    }

    public void setAirsBeforeSeason(Integer airsBeforeSeason) {
        this.airsBeforeSeason = airsBeforeSeason;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getAbsoluteNumber() {
        return absoluteNumber;
    }

    public void setAbsoluteNumber(Integer absoluteNumber) {
        this.absoluteNumber = absoluteNumber;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getAired() {
        return aired;
    }

    public void setAired(String aired) {
        this.aired = aired;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getImageType() {
        return imageType;
    }

    public void setImageType(Integer imageType) {
        this.imageType = imageType;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Long getIsMovie() {
        return isMovie;
    }

    public void setIsMovie(Long isMovie) {
        this.isMovie = isMovie;
    }
    
}
