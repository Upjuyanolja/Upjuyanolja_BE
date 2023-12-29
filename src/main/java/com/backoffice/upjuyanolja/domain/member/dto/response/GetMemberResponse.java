package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record GetMemberResponse(
    long memberId,
    String email,
    String name,
    String phoneNumber
) {

    public static GetMemberResponse of(Member member) {
        return GetMemberResponse.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .phoneNumber(member.getPhone())
            .build();
    }
}
