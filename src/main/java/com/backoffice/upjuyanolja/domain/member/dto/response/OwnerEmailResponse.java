package com.backoffice.upjuyanolja.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerEmailResponse {

    private String verificationCode;

    @Builder
    public OwnerEmailResponse(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
