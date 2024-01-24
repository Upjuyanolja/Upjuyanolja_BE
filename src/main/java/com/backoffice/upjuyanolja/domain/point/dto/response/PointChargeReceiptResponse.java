package com.backoffice.upjuyanolja.domain.point.dto.response;

import lombok.Builder;

@Builder
public record PointChargeReceiptResponse(
    String orderId,
    String tradeAt,
    long amount

) {

    public static PointChargeReceiptResponse of(String orderId, String tradeAt, long amount) {
        return PointChargeReceiptResponse.builder()
            .orderId(orderId)
            .tradeAt(tradeAt)
            .amount(amount)
            .build();
    }
}
