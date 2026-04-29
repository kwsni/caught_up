package com.kwsni.caught_up.tvdb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.EpisodeRepository;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepo;

    public EpisodeService(EpisodeRepository episodeRepo) {
        this.episodeRepo = episodeRepo;
    }

    public Map<Integer, List<Episode>> getEpisodesGroupedBySeason(Series series) {
        return episodeRepo.findBySeries(series, Sort.by("seasonNumber", "number").ascending())
            .stream().collect(Collectors.groupingBy(Episode::getSeasonNumber));
    }
}
