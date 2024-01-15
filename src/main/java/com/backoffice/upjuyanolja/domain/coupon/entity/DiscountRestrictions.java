package com.backoffice.upjuyanolja.domain.coupon.entity;

import lombok.Getter;

@Getter
public enum DiscountRestrictions {

    MIN_WON_DISCOUNT(1000),
    MAX_WON_DISCOUNT(50000),

    MIN_DISCOUNT_RATE(1),
    MAX_DISCOUNT_RATE(50);

    private final int limit;

    DiscountRestrictions(int limit) {
        this.limit = limit;
    }

    public static int getMinPrice() {
        return MIN_WON_DISCOUNT.limit;
    }

    public static int getMaxPrice() {
        return MAX_WON_DISCOUNT.limit;
    }

    public static int getMinRate() {
        return MIN_DISCOUNT_RATE.limit;
    }

    public static int getMaxRate() {
        return MAX_DISCOUNT_RATE.limit;
    }
}