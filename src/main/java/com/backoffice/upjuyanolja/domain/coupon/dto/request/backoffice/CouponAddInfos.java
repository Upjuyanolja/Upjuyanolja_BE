package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;

public record CouponAddInfos(
    Long couponId,
    CouponStatus status,
    DiscountType discountType,
    int discount,
    int dayLimit,
    int buyQuantity,
    CouponType couponType,
    int eachPoint
) {
}
