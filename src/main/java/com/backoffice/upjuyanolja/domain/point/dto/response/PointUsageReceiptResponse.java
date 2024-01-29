package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import java.time.format.DateTimeFormatter;
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
        PointUsage pointUsages, String accommodationName, List<PointUsageDetailReceiptResponse> orders
    ) {
        return PointUsageReceiptResponse.builder()
            .orderId(pointUsages.getOrderName())
            .tradeAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(pointUsages.getOrderDate()))
            .accommodationName(accommodationName)
            .orders(orders)
            .build();
    }
}
