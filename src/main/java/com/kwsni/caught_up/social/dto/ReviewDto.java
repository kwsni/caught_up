package com.kwsni.caught_up.social.dto;

import com.kwsni.caught_up.social.model.Member;

public record ReviewDto(Member author, String content, boolean isSpoiler) {

}
