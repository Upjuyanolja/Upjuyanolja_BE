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

    @NotBlank(message = "이름을 입력해주세요")
    @Size(min = 2, max = 12, message = "올바른 이름을 입력해주세요")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
    private String phone;

    @Nullable
    private String imageUrl;

    @Builder
    public OwnerSignupRequest(String email, String name, String password, String phone,
        @Nullable String imageUrl) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }
}
