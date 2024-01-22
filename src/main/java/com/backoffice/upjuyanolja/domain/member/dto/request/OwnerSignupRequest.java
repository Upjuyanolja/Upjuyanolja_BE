package com.backoffice.upjuyanolja.domain.member.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerSignupRequest {

    @Email(message = "Email을 입력해주세요")
    @NotBlank
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "비밀번호는 8~20자 영문 대 소문자, 숫자를 사용하세요.")
    private String password;


    @Builder
    public OwnerSignupRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
