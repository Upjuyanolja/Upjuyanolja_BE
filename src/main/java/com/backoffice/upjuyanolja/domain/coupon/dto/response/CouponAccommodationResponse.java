package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
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
    List<String> rooms
) {

    public static CouponAccommodationResponse from(Coupon coupon, List<String> rooms) {
        return CouponAccommodationResponse.builder()
            .id(coupon.getId())
            .couponName(DiscountType.makeTitleName(coupon.getDiscountType(), coupon.getDiscount()))
            .endDate(coupon.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .rooms(rooms)
            .build();
    }

}
