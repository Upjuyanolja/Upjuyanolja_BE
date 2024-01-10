package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import lombok.Builder;

@Builder
public record AccommodationOptionResponse(
    boolean cooking,
    boolean parking,
    boolean pickup,
    boolean barbecue,
    boolean fitness,
    boolean karaoke,
    boolean sauna,
    boolean sports,
    boolean seminar
) {

    public static AccommodationOptionResponse of(AccommodationOption accommodationOption) {
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
