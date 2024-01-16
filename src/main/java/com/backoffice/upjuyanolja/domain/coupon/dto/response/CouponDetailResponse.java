package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.makeDetailName;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
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
            .name(makeDetailName(coupon.getDiscountType(), coupon.getDiscount()))
            .price(discount)
            .build();
    }

}
