package com.backoffice.upjuyanolja.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponseDto {

    private String accessToken;

    private String refreshToken;

    @Builder
    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}