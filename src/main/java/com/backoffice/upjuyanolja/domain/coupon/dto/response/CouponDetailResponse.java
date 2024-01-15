package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import lombok.Builder;

@Builder
public record CouponDetailResponse(
    Long id,
    String name,
    int price
) {

    public static CouponDetailResponse of(Coupon coupon, int discount) {
        return CouponDetailResponse.builder()
            .id(coupon.getId())
            .name(DiscountType.makeDetailName(coupon.getDiscountType(), coupon.getDiscount()))
            .price(discount)
            .build();
    }

}