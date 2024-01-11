package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpResponse {

    private Long id;

    private String email;

    private String name;

    private String phone;

    @Builder
    public SignUpResponse(Long id, String email, String name, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }


    public static SignUpResponse fromEntity(Member member) {
        return SignUpResponse.builder()
            .id(member.getId())
            .name(member.getName())
            .email(member.getEmail())
            .phone(member.getPhone())
            .build();
    }
}
