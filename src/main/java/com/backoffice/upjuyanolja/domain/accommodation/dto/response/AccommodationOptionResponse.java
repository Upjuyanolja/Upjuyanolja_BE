package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import lombok.Builder;

@Builder
public record AccommodationOptionResponse(
    Boolean cooking,
    Boolean parking,
    Boolean pickup,
    Boolean barbecue,
    Boolean fitness,
    Boolean karaoke,
    Boolean sauna,
    Boolean sports,
    Boolean seminar
) {

    public static AccommodationOptionResponse from(
        AccommodationOption accommodationOption
    ) {
        return AccommodationOptionResponse.builder()
            .cooking(accommodationOption.isCooking())
            .parking(accommodationOption.isParking())
            .pickup(accommodationOption.isPickup())
            .barbecue(accommodationOption.isBarbecue())
            .fitness(accommodationOption.isFitness())
            .karaoke(accommodationOption.isKaraoke())
            .sauna(accommodationOption.isSauna())
            .sports(accommodationOption.isSports())
            .seminar(accommodationOption.isSeminar())
            .build();
    }
}
