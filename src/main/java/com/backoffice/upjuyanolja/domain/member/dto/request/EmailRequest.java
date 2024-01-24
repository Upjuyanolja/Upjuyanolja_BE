package com.backoffice.upjuyanolja.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record EmailRequest(
    @NotBlank(message = "이메일을 입력하세요.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "이메일 형식에 맞게 입력해주세요.")
    String email
) {

}
