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

    public static AccommodationOptionResponse of(
        AccommodationOption option
    ) {
        return AccommodationOptionResponse.builder()
            .cooking(option.isCooking())
            .parking(option.isParking())
            .pickup(option.isPickup())
            .barbecue(option.isBarbecue())
            .fitness(option.isFitness())
            .karaoke(option.isKaraoke())
            .sauna(option.isSauna())
            .sports(option.isSports())
            .seminar(option.isSeminar())
            .build();
    }
}
