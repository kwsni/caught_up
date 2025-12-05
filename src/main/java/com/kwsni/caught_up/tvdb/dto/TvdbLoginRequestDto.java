package com.kwsni.caught_up.tvdb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record TvdbLoginRequestDto(
    String apikey,
    @JsonInclude(Include.NON_EMPTY)
    String pin) {
    
}
