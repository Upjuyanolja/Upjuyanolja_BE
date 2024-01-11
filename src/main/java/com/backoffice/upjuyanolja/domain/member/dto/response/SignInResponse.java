package com.backoffice.upjuyanolja.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignInResponse {

    private String accessToken;

    private String refreshToken;

    private MemberResponse memberResponse;

    @Builder
    public SignInResponse(String accessToken, String refreshToken, Long id, String name, String email, String phone) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberResponse = MemberResponse.builder()
            .id(id)
            .name(name)
            .email(email)
            .phone(phone)
            .build();
    }
}
