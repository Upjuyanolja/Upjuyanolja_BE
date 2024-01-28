package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import lombok.Builder;

@Builder
public record CouponMakeQueryDto(
    Long accommodationId,
    String accommodationName,
    Long roomId,
    String roomName,
    Integer roomPrice
) {
}
