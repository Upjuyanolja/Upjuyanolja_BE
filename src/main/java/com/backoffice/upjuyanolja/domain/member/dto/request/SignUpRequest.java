package com.backoffice.upjuyanolja.domain.member.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignUpRequest(
    @Email(message = "Email을 입력해주세요")
    @NotBlank
    String email,

    @NotBlank(message = "이름을 입력해주세요")
    @Size(min = 2, max = 12, message = "올바른 이름을 입력해주세요")
    String name,

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "비밀번호는 8~20자 영문 대 소문자, 숫자를 사용하세요.")
    String password,

    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
    String phone,

    @Nullable
    String imageUrl

) {

}
