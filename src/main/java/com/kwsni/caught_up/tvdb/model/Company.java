package com.kwsni.caught_up.tvdb.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long tvdbId;
    
    private String name;
    
    private String country;
    
    @Nullable
    private Long primaryCompanyType;
    
    @Nullable
    private String activeDate;
    
    @Nullable
    private String inactiveDate;
    
    private String slug;

    protected Company() {}

    public Company(Long tvdbId, String name, String country, Long primaryCompanyType,
            String activeDate, String inactiveDate, String slug) {
        this.tvdbId = tvdbId;
        this.name = name;
        this.country = country;
        this.primaryCompanyType = primaryCompanyType;
        this.activeDate = activeDate;
        this.inactiveDate = inactiveDate;
        this.slug = slug;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getPrimaryCompanyType() {
        return primaryCompanyType;
    }

    public void setPrimaryCompanyType(Long primaryCompanyType) {
        this.primaryCompanyType = primaryCompanyType;
    }

    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }

    public String getInactiveDate() {
        return inactiveDate;
    }

    public void setInactiveDate(String inactiveDate) {
        this.inactiveDate = inactiveDate;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    
}
