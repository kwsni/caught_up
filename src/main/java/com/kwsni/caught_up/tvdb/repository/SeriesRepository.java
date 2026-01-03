package com.kwsni.caught_up.tvdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.tvdb.model.Series;

public interface SeriesRepository extends JpaRepository<Series, Integer>{}
