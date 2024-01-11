package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;

@Builder
public record CouponAccommodationResponse(
    Long id,
    String couponName,
    String endDate,
    List<String> roomNames
) {

    public static CouponAccommodationResponse from(Coupon coupon, List<String> roomNames) {
        return CouponAccommodationResponse.builder()
            .id(coupon.getId())
            .couponName(DiscountType.makeName(
                coupon.getDiscountType(), coupon.getDiscount(),
                coupon.getDiscountType().getShortName()
            ))
            .endDate(coupon.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .roomNames(roomNames)
            .build();
    }

}
