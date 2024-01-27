package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import lombok.Builder;

@Builder
public record PointUsageCouponReceiptResponse(
    String name,
    int count,
    int totalPrice
) {

    public static PointUsageCouponReceiptResponse of(
        CouponIssuance couponIssuance
    ) {
        Coupon coupon = couponIssuance.getCoupon();

        return PointUsageCouponReceiptResponse.builder()
            .name(coupon.getDiscount() + "원 쿠폰 | " + coupon.getDiscount() / 10 + "p")
            .count(couponIssuance.getQuantity())
            .totalPrice(couponIssuance.getAmount())
            .build();
    }
}
