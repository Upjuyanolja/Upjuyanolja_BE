package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse{

    private String email;
    private String name;

    public static SignUpResponse fromEntity(Member member){
        return SignUpResponse.builder()
            .name(member.getName())
            .email(member.getEmail())
            .build();
    }
}
