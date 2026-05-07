package com.kwsni.caught_up.tvdb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.kwsni.caught_up.tvdb.model.Series;
import com.kwsni.caught_up.tvdb.repository.PopularityScore;
import com.kwsni.caught_up.tvdb.repository.SeriesRepository;

@Service
public class SeriesService {
    private final SeriesRepository seriesRepo;

    public SeriesService(SeriesRepository seriesRepo) {
        this.seriesRepo = seriesRepo;
    }

    public Page<Series> getPopularSeries() {
        Pageable sortedByPopularityDesc = PageRequest.of(0, 4, Sort.by("score").descending());
        return seriesRepo.findAll(sortedByPopularityDesc);
    }

    public Page<Series> getSortedSeriesPage(
        String[] sort,
        String size,
        int page
    ) {
        String sortField = sort[0];
        String sortDir = sort[1];

        Direction dir = sortDir.equals("desc") ? Direction.DESC : Direction.ASC;
        boolean isLarge = size.equals("lg");
        int pageSize = isLarge ? 18 : 72;
        
        Pageable sortBy = PageRequest.of(page, pageSize, Sort.by(Sort.Order.by(sortField).with(dir).nullsLast()));
        return seriesRepo.findAll(sortBy);

    }

    public Optional<Series> getSeries(String slug) {
        return seriesRepo.findBySlug(slug);
    }

    public Optional<Series> getSeries(Long tvdbId) {
        return seriesRepo.findById(tvdbId);
    }

    public List<PopularityScore> getPopularSeriesScores() {
        return seriesRepo.queryAllByOrderByTvdbIdAsc();
    }
}
