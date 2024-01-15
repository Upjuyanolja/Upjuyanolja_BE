package com.backoffice.upjuyanolja.domain.coupon.entity;

import lombok.Getter;

@Getter
public enum CouponType {
    WEEKDAYS("평일"),
    WEEKENDS("주말"),
    ALL_DAYS("상시");

    private final String indicates;

    CouponType(final String indicates) {
        this.indicates = indicates;
    }
}
