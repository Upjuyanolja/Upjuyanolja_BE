package com.backoffice.upjuyanolja.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerEmailRequest {

    @Email(message = "Email을 입력해주세요")
    @NotBlank
    private String email;


    @Builder
    public OwnerEmailRequest(String email) {
        this.email = email;
    }
}
