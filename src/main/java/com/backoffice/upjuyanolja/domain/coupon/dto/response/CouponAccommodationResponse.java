package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.*;

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
    List<String> roomNames
) {

    public static CouponAccommodationResponse of(
        final Coupon coupon,
        final List<String> roomNames
    ) {
        return CouponAccommodationResponse.builder()
            .id(coupon.getId())
            .couponName(DiscountType.makeShortName(coupon.getDiscountType(), coupon.getDiscount()))
            .endDate(coupon.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .roomNames(roomNames)
            .build();
    }

}
