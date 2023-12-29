package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import lombok.Builder;

@Builder
public record CouponRoomResponse(
    Long id,
    String name,
    Integer price
) {

    public static CouponRoomResponse from(Coupon coupon, Integer couponRoomPrice) {
        return CouponRoomResponse.builder()
            .id(coupon.getId())
            .name(coupon.getCouponPrice() + " " + coupon.getType().getName())
            .price(couponRoomPrice)
            .build();
    }

}
