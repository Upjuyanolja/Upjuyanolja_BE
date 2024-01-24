package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import lombok.Builder;

@Builder
public record PointChargeReceiptResponse(
    String orderId,
    String tradeAt,
    long amount

) {

    public static PointChargeReceiptResponse of(PointCharges pointCharges) {
        return PointChargeReceiptResponse.builder()
            .orderId(pointCharges.getOrderName())
            .tradeAt(pointCharges.getChargeDate().toString())
            .amount(pointCharges.getChargePoint())
            .build();
    }
}
