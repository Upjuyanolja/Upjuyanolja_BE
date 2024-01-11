package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import lombok.Builder;

@Builder
public record CouponRoomResponse(
    Long id,
    String name,
    int price
) {

    public static CouponRoomResponse from(Coupon coupon, int couponRoomPrice) {
        return CouponRoomResponse.builder()
            .id(coupon.getId())
            .name(coupon.getDiscount() + coupon.getDiscountType().getTitleName())
            .price(couponRoomPrice)
            .build();
    }

}
