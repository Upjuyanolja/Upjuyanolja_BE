package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Owner;
import lombok.Builder;

@Builder
public record OwnerInfoResponse(
    long memberId,
    String email,
    String name,
    String phoneNumber
) {

    public static OwnerInfoResponse of(Owner owner) {
        return OwnerInfoResponse.builder()
            .memberId(owner.getId())
            .email(owner.getEmail())
            .name(owner.getName())
            .phoneNumber(owner.getPhone())
            .build();
    }
}
