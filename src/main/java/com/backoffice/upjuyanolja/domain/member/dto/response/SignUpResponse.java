package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpResponse {

    private String email;

    private String name;

    @Builder
    public SignUpResponse(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static SignUpResponse fromEntity(Member member) {
        return SignUpResponse.builder()
            .name(member.getName())
            .email(member.getEmail())
            .build();
    }
}
