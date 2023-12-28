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
            .cooking(accommodationOption.getCooking())
            .parking(accommodationOption.getParking())
            .pickup(accommodationOption.getPickup())
            .barbecue(accommodationOption.getBarbecue())
            .fitness(accommodationOption.getFitness())
            .karaoke(accommodationOption.getKaraoke())
            .sauna(accommodationOption.getSauna())
            .sports(accommodationOption.getSports())
            .seminar(accommodationOption.getSeminar())
            .build();
    }
}
