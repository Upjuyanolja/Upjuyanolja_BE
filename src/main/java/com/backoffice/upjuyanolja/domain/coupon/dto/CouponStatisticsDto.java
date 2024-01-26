package com.backoffice.upjuyanolja.domain.coupon.dto;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import lombok.Builder;

@Builder
public record CouponStatisticsDto(
    Accommodation accommodation,
    long total,
    long used,
    long stock
) {

    public static CouponStatistics toEntity(
        Accommodation accommodation,
        long total,
        long used,
        long stock
    ) {
        return CouponStatistics.builder()
            .accommodation(accommodation)
            .total(total)
            .used(used)
            .stock(stock)
            .build();
    }
}
