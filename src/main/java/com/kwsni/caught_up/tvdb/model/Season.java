package com.kwsni.caught_up.tvdb.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long tvdbId;

    private String name;
    
    private Long seriesId;

    private String year;
    
    private Integer number;
    
    private String type;
    
    private String image;
    
    private String imageType;
    
    @OneToMany
    private List<Company> companies;
    
}
