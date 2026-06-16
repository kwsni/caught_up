package com.kwsni.caught_up.tvdb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.transaction.annotation.Transactional;

import com.kwsni.caught_up.tvdb.model.Series;

@Transactional
public interface SeriesRepository extends JpaRepository<Series, Long> {
    public Optional<Series> findBySlug(String slug);
    public List<PopularityScore> queryAllByOrderByTvdbIdAsc();
    @NativeQuery(value = """
        SELECT tvdb_id, name, year, overview, slug, score, image,
            first_aired, last_aired, next_aired, country, last_updated,
            (SELECT AVG(r.rating) FROM review r WHERE r.series_tvdb_id = tvdb_id) as avgrating,
            ts_rank(search_vector, websearch_to_tsquery('english', ?1), 1) as rank
        FROM series
        WHERE search_vector @@ websearch_to_tsquery('english', ?1)
        ORDER BY rank DESC, score DESC""")
    public List<Series> findBySearchQuery(String query);
}
