package com.backoffice.upjuyanolja.domain.point.dto.response;

import lombok.Builder;

@Builder
public record PointUsageCouponReceiptResponse(
    String name,
    int count,
    int totalPrice
) {

    public static PointUsageCouponReceiptResponse of(
        String name, int count, int totalPrice
    ) {
        return PointUsageCouponReceiptResponse.builder()
            .name(name)
            .count(count)
            .totalPrice(totalPrice)
            .build();
    }
}
