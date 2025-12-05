package com.kwsni.caught_up.tvdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.tvdb.model.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    
}
