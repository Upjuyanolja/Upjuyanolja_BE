package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CouponRoomDetailResponse(
    String roomName,
    List<CouponRoomResponse> couponRooms
) {

    public static CouponRoomDetailResponse from(String roomName, List<CouponRoomResponse> response) {
        return CouponRoomDetailResponse.builder()
            .roomName(roomName)
            .couponRooms(response)
            .build();
    }

}
