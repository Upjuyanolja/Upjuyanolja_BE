package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record CouponDetailResponse(
    Long id,
    String couponName,
    String endDate,
    String status,
    int count

) {

    public static CouponDetailResponse of(Coupon coupon) {
        return CouponDetailResponse.builder()
            .id(coupon.getId())
            .couponName(coupon.getDiscount() + coupon.getDiscountType().getTitleName())
            .endDate(coupon.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .status(coupon.getCouponStatus().getDescription())
            .count(coupon.getCount())
            .build();
    }

}
