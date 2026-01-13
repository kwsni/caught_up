package com.kwsni.caught_up.tvdb.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

@Transactional
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    public List<Episode> findBySeries(Series series);
    public List<Episode> findBySeries(Series series, Sort sort);
    public List<Episode> findBySeriesAndSeasonNumber(Series series, Integer seasonNumber);
}
