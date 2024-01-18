package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.util.List;
import lombok.Builder;

@Builder
public record CouponAccommodationResponse(
    Long id,
    String roomName,
    List<CouponShortResponse> coupons
) {

    public static CouponAccommodationResponse of(
        Room room,
        List<CouponShortResponse> coupons
    ) {
        return CouponAccommodationResponse.builder()
            .id(room.getId())
            .roomName(room.getName())
            .coupons(coupons)
            .build();
    }
}
