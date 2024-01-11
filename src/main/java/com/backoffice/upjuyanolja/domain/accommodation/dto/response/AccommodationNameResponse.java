package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import lombok.Builder;

@Builder
public record AccommodationNameResponse(
    long id,
    String name
) {

    public static AccommodationNameResponse of(Accommodation accommodation) {
        return AccommodationNameResponse.builder()
            .id(accommodation.getId())
            .name(accommodation.getName())
            .build();
    }
}
