package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import lombok.Builder;

@Builder
public record AccommodationSummaryResponse(
    Long id,
    String name,
    String shortAddress,
    String category,
    int lowestPrice,
    int discountPrice,
    String thumbnail,
    String couponName

) {

    public static AccommodationSummaryResponse from(
        Accommodation accommodation, int lowestPrice,
        int discountPrice, String couponName
    ) {
        return AccommodationSummaryResponse.builder()
            .id(accommodation.getId())
            .name(accommodation.getName())
            .shortAddress(accommodation.getAddress().getShortAddress())
            .category(accommodation.getCategory().getName())
            .lowestPrice(lowestPrice)
            .discountPrice(discountPrice)
            .thumbnail(accommodation.getThumbnail())
            .couponName(couponName)
            .build();
    }
}