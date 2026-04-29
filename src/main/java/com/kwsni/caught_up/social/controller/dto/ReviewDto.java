package com.kwsni.caught_up.social.controller.dto;

import java.time.LocalDate;
import java.util.Set;

import com.kwsni.caught_up.social.model.Member;
import com.kwsni.caught_up.tvdb.model.Series;

public record ReviewDto(
    Member author,
    Series series,
    String content,
    LocalDate watchedOn,
    LocalDate createdOn,
    Double rating,
    Set<Member> likes,
    boolean isSpoiler,
    boolean liked) {

}
