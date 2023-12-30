package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberInfoResponse(
    long memberId,
    String email,
    String name,
    String phoneNumber
) {

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .phoneNumber(member.getPhone())
            .build();
    }
}
