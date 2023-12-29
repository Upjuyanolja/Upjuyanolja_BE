package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
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

    public static CouponAccommodationResponse from(Coupon coupon, Accommodation accommodation) {
        return CouponAccommodationResponse.builder()
            .id(coupon.getId())
            .couponName(coupon.getCouponPrice() + " " + coupon.getType().getName())
            .endDate(coupon.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            .rooms(
                accommodation.getRooms().stream()
                    .filter(room -> coupon.getCouponRooms().contains(room))
                    .map(room -> room.getName())
                    .toList()
            )
            .build();
    }

}
