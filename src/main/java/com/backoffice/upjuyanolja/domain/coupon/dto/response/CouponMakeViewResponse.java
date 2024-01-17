package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CouponMakeViewResponse(
    Long accommodationId,
    String accommodationName,
    List<CouponRoomsResponse> rooms
) {

    public static CouponMakeViewResponse of(
        AccommodationResponse accommodationResponse,
        List<CouponRoomsResponse> roomListRespons
    ) {
        return CouponMakeViewResponse.builder()
            .accommodationId(accommodationResponse.accommodationId())
            .accommodationName(accommodationResponse.accommodationName())
            .rooms(roomListRespons)
            .build();
    }
}
