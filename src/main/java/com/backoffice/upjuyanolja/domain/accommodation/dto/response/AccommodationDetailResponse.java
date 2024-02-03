package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationDetailResponse(
    Long id,
    String name,
    String address,
    String description,
    String category,
    String mainCoupon,
    List<String> images,
    AccommodationOptionResponse option,
    List<RoomResponse> rooms
) {

    public static AccommodationDetailResponse of(
        Accommodation accommodation, String mainCoupon, AccommodationOption option,
        List<String> images, List<RoomResponse> rooms
    ) {
        return AccommodationDetailResponse.builder()
            .id(accommodation.getId())
            .name(accommodation.getName())
            .address(
                accommodation.getAddress() + "\n"
                    + accommodation.getDetailAddress()
            )
            .description(accommodation.getDescription())
            .category(accommodation.getCategory().getName())
            .mainCoupon(mainCoupon)
            .option(AccommodationOptionResponse.of(option))
            .images(images)
            .rooms(rooms)
            .build();
    }
}
