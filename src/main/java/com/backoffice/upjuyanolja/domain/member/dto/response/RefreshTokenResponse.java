package com.backoffice.upjuyanolja.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshTokenResponse {

    private String refreshToken;

    public RefreshTokenResponse(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
