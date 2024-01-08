package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationInfoResponse(
    long accommodationId,
    String name,
    String type,
    String address,
    String description,
    List<AccommodationImageResponse> images,
    AccommodationOptionResponse option,
    List<RoomInfoResponse> rooms
) {

    public static AccommodationInfoResponse of(Accommodation accommodation) {
        return AccommodationInfoResponse.builder()
            .accommodationId(accommodation.getId())
            .name(accommodation.getName())
            .type(accommodation.getType().name())
            .address(accommodation.getAddress().getAddress()
                + " "
                + accommodation.getAddress().getDetailAddress())
            .images(AccommodationImageResponse.of(accommodation.getImages()))
            .option(AccommodationOptionResponse.of(accommodation.getOption()))
            .build();
    }
}
