package com.backoffice.upjuyanolja.domain.coupon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CouponProperties {

    private static int minFlatAmount;
    private static int maxFlatAmount;
    private static int minDiscountRate;
    private static int maxDiscountRate;
    private static int dayLimit;

    @Autowired
    public void init(
        @Value("${coupon.constraints.min-flat-amount}") int minPrice,
        @Value("${coupon.constraints.max-flat-amount}") int maxPrice,
        @Value("${coupon.constraints.min-discount-rate}") int minRate,
        @Value("${coupon.constraints.max-discount-rate}") int maxRate,
        @Value("${coupon.constraints.day-limit}") int day
    ) {
        minFlatAmount = minPrice;
        maxFlatAmount = maxPrice;
        minDiscountRate = minRate;
        maxDiscountRate = maxRate;
        dayLimit = day;
    }

    public static int getMinPrice() {
        return minFlatAmount;
    }

    public static int getMaxPrice() {
        return maxFlatAmount;
    }

    public static int getMinRate() {
        return minDiscountRate;
    }

    public static int getMaxRate() {
        return maxDiscountRate;
    }

    public static int getDayLimit() {
        return dayLimit;
    }
}
