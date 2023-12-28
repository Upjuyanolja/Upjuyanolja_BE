package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import lombok.Builder;

@Builder
public record AccommodationSummaryResponse(
    Long id,
    String name,
    String shortAddress,
    String category,
    Integer lowestPrice,
    Integer discountPrice,
    String thumbnail,
    Boolean soldOut,
    String coupon

) {

    public static AccommodationSummaryResponse from(
        Accommodation accommodation, Boolean isSoldOut, Integer lowestPrice, Integer discountPrice, String couponName
    ) {
        return AccommodationSummaryResponse.builder()
            .id(accommodation.getId())
            .name(accommodation.getName())
            .shortAddress(accommodation.getAddress().getShortAddress())
            .category(accommodation.getCategory().getName())
            .lowestPrice(lowestPrice)
            .discountPrice(discountPrice)
            .thumbnail(accommodation.getThumbnail())
            .soldOut(isSoldOut)
            .coupon(couponName)
            .build();
    }
}
