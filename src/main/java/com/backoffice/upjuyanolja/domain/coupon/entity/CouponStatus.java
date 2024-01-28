package com.backoffice.upjuyanolja.domain.coupon.entity;

import java.util.function.Predicate;
import lombok.Getter;

@Getter
public enum CouponStatus {

    ENABLE("발급 중"),
    DISABLE("발급 중지"),
    SOLD_OUT("소진");

    private final String description;

    CouponStatus(final String description) {
        this.description = description;
    }

    public static final Predicate<CouponStatus> isRedeemCoupon = (status) -> {
        if (status.equals(SOLD_OUT)) {
            return false;
        }
        return true;
    };

}
