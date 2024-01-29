package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import java.util.List;
import lombok.Builder;

@Builder
public record PointChargeDetailResponse(
    long id,
    String category,
    String type,
    String status,
    String name,
    long trade,
    long amount,
    PointChargeReceiptResponse receipt

) {

    public static PointChargeDetailResponse of(
        PointCharges pointCharges, PointChargeReceiptResponse receipt
    ) {
        return PointChargeDetailResponse.builder()
            .id(pointCharges.getId())
            .category(pointCharges.getPointStatus().getCategory())
            .type(pointCharges.getPointStatus().getType())
            .status(pointCharges.getPointStatus().getStatus())
            .name("포인트 충전")
            .trade(pointCharges.getChargePoint())
            .amount(pointCharges.getChargePoint())
            .receipt(receipt)
            .build();
    }
}
