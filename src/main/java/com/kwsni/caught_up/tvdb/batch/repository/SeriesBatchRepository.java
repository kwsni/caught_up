package com.kwsni.caught_up.tvdb.batch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.tvdb.model.Series;

public interface SeriesBatchRepository extends JpaRepository<Series, Long> {
    public Optional<Series> findBySlug(String slug);
}
