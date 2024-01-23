package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import lombok.Builder;

@Builder
public record PointChargeResponse(
    String orderId,
    String tradeAt,
    String orderName,
    long amount
) {

    public static PointChargeResponse of(
        PointCharges pointCharges
    ) {
        return PointChargeResponse.builder()
            .orderId(pointCharges.getOrderName())
            .tradeAt(pointCharges.getChargeDate().toString())
            .orderName(pointCharges.getPaymentName())
            .amount(pointCharges.getChargePoint())
            .build();
    }
}
