package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import lombok.Builder;

@Builder
public record RevenueInfo(
    String revenueDate,
    long couponRevenue,
    long normalRevenue
) {
}
