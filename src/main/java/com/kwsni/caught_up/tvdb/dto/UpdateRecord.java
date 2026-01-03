package com.kwsni.caught_up.tvdb.dto;

import java.util.Optional;

import com.kwsni.caught_up.tvdb.model.Episode;
import com.kwsni.caught_up.tvdb.model.Series;

public record UpdateRecord(
    Optional<Series> series,
    Optional<Episode> episode,
    Optional<Long> tvdbIdToDelete
) {}
