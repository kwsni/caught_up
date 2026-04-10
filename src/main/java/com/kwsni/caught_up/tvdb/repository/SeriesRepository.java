package com.kwsni.caught_up.tvdb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.kwsni.caught_up.tvdb.model.Series;

@Transactional
public interface SeriesRepository extends JpaRepository<Series, Long> {
    public Optional<Series> findBySlug(String slug);
}
