package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationInfoResponse(
    long accommodationId,
    String name,
    String category,
    String address,
    String description,
    List<AccommodationImageResponse> images,
    AccommodationOptionResponse option,
    List<RoomInfoResponse> rooms
) {

    public static AccommodationInfoResponse of(
        Accommodation accommodation, AccommodationOption option, List<AccommodationImage> images,
        List<RoomInfoResponse> rooms
    ) {
        return AccommodationInfoResponse.builder()
            .accommodationId(accommodation.getId())
            .name(accommodation.getName())
            .category(accommodation.getCategory().getName())
            .description(accommodation.getDescription())
            .address(accommodation.getAddress()
                + " "
                + accommodation.getDetailAddress())
            .option(AccommodationOptionResponse.of(option))
            .images(AccommodationImageResponse.of(images))
            .rooms(rooms)
            .build();
    }
}
