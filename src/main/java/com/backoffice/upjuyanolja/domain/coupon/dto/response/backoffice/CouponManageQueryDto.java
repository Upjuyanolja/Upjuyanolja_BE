package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CouponManageQueryDto(
    Long accommodationId,
    String accommodationName,
    LocalDate endDate,
    Long roomId,
    String roomName,
    int roomPrice,
    Long couponId,
    CouponStatus couponStatus,
    DiscountType discountType,
    int discount,
    int dayLimit,
    int stock,
    CouponType couponType
) {
}
