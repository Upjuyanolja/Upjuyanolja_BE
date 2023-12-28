package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import lombok.Builder;

@Builder
public record CouponResponse(
    Long id,
    String name,
    Integer price
) {

    public static CouponResponse from(Coupon coupon) {
        return CouponResponse.builder()
            .id(coupon.getId())
            .name(coupon.getType().getName())
            .price(coupon.getCouponPrice())
            .build();
    }

}
