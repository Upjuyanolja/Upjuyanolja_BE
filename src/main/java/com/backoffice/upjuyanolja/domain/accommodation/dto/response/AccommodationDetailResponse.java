package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationDetailResponse(
    Long id,
    String name,
    String shortAddress,
    String phoneNumber,
    String mapX,
    String mapY,
    String description,
    String category,
    String mainCoupon,
    List<String> images,
    AccommodationOptionResponse accommodationOption,
    List<RoomResponse> rooms
) {

    public static AccommodationDetailResponse from(
        Accommodation accommodation, List<RoomResponse> rooms
    ) {
        return AccommodationDetailResponse.builder()
            .id(accommodation.getId())
            .name(accommodation.getName())
            .shortAddress(accommodation.getAddress().getShortAddress())
            .phoneNumber(accommodation.getPhoneNumber())
            .mapX(String.valueOf(accommodation.getAddress().getMapX()))
            .mapY(String.valueOf(accommodation.getAddress().getMapY()))
            .description(accommodation.getDescription())
            .category(accommodation.getCategory().getName())
            .images(
                accommodation.getAccommodationImages().stream()
                    .map(image -> image.getUrl())
                    .toList()
            )
            .accommodationOption(
                AccommodationOptionResponse.from(accommodation.getAccommodationOption())
            )
            .rooms(rooms)
            .build();
    }
}
