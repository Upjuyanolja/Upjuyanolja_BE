package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import lombok.Builder;

@Builder
public record CouponStatisticsResponse(
    Long accommodationId,
    long total,
    long used,
    long stock
) {
    public static CouponStatisticsResponse from(CouponStatistics statistics) {
        return CouponStatisticsResponse.builder()
            .accommodationId(statistics.getId())
            .total(statistics.getTotal())
            .used(statistics.getUsed())
            .stock(statistics.getStock())
            .build();
    }
}
