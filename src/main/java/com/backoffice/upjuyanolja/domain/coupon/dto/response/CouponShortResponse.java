package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.makeDetailName;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CouponShortResponse(
    Long id,
    String name,
    LocalDate endDate
) {

    public static CouponShortResponse of(Coupon coupon) {
        return CouponShortResponse.builder()
            .id(coupon.getId())
            .name(makeDetailName(coupon.getDiscountType(), coupon.getDiscount()))
            .endDate(coupon.getEndDate())
            .build();
    }

}
