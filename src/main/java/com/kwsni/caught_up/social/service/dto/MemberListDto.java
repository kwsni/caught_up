package com.kwsni.caught_up.social.service.dto;

import java.util.List;

import com.kwsni.caught_up.social.model.Member;

public record MemberListDto(
    Member principalMember,
    List<Member> memberList,
    boolean hasPrev,
    boolean hasNext
) {
    
}
