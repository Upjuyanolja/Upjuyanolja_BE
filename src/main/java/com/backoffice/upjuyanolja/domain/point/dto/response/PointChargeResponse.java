package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import java.time.format.DateTimeFormatter;
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
            .tradeAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(pointCharges.getChargeDate()))
            .orderName(pointCharges.getChargePoint()+"원")
            .amount(pointCharges.getChargePoint())
            .build();
    }
}
