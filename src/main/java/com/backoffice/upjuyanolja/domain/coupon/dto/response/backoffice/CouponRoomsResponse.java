package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import lombok.Builder;

@Builder
public record CouponRoomsResponse(
    Long roomId,
    String roomName,
    Integer roomPrice
) {

    public static CouponRoomsResponse of(
        Long roomId,
        String roomName,
        Integer roomPrice
    ) {
        return CouponRoomsResponse.builder()
            .roomId(roomId)
            .roomName(roomName)
            .roomPrice(roomPrice)
            .build();
    }
}
