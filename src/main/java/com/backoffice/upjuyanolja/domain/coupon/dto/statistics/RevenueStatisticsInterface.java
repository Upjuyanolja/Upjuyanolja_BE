package com.backoffice.upjuyanolja.domain.coupon.dto.statistics;

import java.time.LocalDate;

public interface RevenueStatisticsInterface {

    long getId();

    LocalDate getRevenueDate();

    long getCouponRevenue();

    long getRegularRevenue();
}
