package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record CouponDetailResponse(
    Long id,
    String couponName,
    String startDate,
    String endDate,
    String status,
    int count

) {

    public static CouponDetailResponse from(Coupon coupon) {
        return CouponDetailResponse.builder()
            .id(coupon.getId())
            .couponName(coupon.getCouponPrice() + coupon.getType().getName())
            .startDate(coupon.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .endDate(coupon.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .status(coupon.getStatus().getDescription())
            .count(coupon.getCount())
            .build();
    }

}
