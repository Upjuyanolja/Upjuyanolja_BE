package com.backoffice.upjuyanolja.domain.coupon.entity;

import lombok.Getter;

@Getter
public enum CouponStatus {

    ACTIVE("발급 중"),
    DISABLE("발급 중지"),
    SOLD_OUT("소진");

    private final String description;

    CouponStatus(String description) {
        this.description = description;
    }

}