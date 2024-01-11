package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
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
            .couponName(DiscountType.makeTitleName(coupon.getDiscountType(), coupon.getDiscount()))
            .endDate(coupon.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .status(coupon.getCouponStatus().getDescription())
            .count(coupon.getCount())
            .build();
    }

}
