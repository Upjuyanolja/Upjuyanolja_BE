package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CouponRoomDetailResponse(
    String roomName,
    String type,
    List<CouponRoomResponse> couponRooms
) {

    public static CouponRoomDetailResponse from(
        String roomName, String type, List<CouponRoomResponse> response
    ) {
        return CouponRoomDetailResponse.builder()
            .roomName(roomName)
            .type(type)
            .couponRooms(response)
            .build();
    }

}
