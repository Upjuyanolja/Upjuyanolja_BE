package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import lombok.Builder;

@Builder
public record AccommodationResponse(
    Long accommodationId,
    String accommodationName
) {

    public static AccommodationResponse of(
        Long accommodationId,
        String accommodationName
    ) {
        return AccommodationResponse.builder()
            .accommodationId(accommodationId)
            .accommodationName(accommodationName)
            .build();
    }
}
