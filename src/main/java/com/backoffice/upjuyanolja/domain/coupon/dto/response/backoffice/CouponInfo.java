package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import lombok.Builder;

@Builder
public record CouponInfo(

    Long couponId,
    CouponStatus status,
    String couponName,
    int appliedPrice, // 쿠폰 적용 가격
    DiscountType discountType,
    int discount,
    int dayLimit,
    int quantity, // coupon stock
    CouponType couponType
) {

    public static CouponInfo from(Coupon coupon, int roomPrice) {
        return CouponInfo.builder()
            .couponId(coupon.getId())
            .appliedPrice(DiscountType.makePaymentPrice(
                coupon.getDiscountType(), roomPrice, coupon.getDiscount())
            )
            .couponName(DiscountType.makeListName(
                coupon.getDiscountType(), coupon.getDiscount())
            )
            .discountType(coupon.getDiscountType())
            .discount(coupon.getDiscount())
            .dayLimit(coupon.getDayLimit())
            .couponType(coupon.getCouponType())
            .status(coupon.getCouponStatus())
            .build();
    }
}
