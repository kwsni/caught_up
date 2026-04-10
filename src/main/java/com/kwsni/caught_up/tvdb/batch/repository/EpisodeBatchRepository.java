package com.kwsni.caught_up.tvdb.batch.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

public interface EpisodeBatchRepository extends JpaRepository<Episode, Long> {
    public List<Episode> findBySeries(Series series);
    public List<Episode> findBySeries(Series series, Sort sort);
    public List<Episode> findBySeriesAndSeasonNumber(Series series, Integer seasonNumber);
}
