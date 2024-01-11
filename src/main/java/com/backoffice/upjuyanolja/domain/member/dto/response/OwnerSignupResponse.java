package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.entity.Owner;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerSignupResponse {

    private Long id;

    private String email;

    private String name;

    private String phone;

    private String imageUrl;

    @Builder
    public OwnerSignupResponse(Long id, String email, String name, String phone, String imageUrl) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public static OwnerSignupResponse fromEntity(Member member){
        return OwnerSignupResponse.builder()
            .id(member.getId())
            .name(member.getName())
            .email(member.getEmail())
            .phone(member.getPhone())
            .imageUrl(member.getImageUrl())
            .build();
    }
}
