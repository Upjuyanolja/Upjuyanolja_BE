package com.backoffice.upjuyanolja.domain.point.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PointUsageReceiptResponse(
    String orderId,
    String tradeAt,
    String accommodationName,
    List<PointUsageDetailReceiptResponse> orders

) {

    public static PointUsageReceiptResponse of(
        String orderId, String tradeAt,
        String accommodationName, List<PointUsageDetailReceiptResponse> orders
    ) {
        return PointUsageReceiptResponse.builder()
            .orderId(orderId)
            .tradeAt(tradeAt)
            .accommodationName(accommodationName)
            .orders(orders)
            .build();
    }
}
