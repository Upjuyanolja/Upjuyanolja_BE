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
    long trade,
    long amount,
    List<PointChargeReceiptResponse> receipt

) {

    public static PointChargeDetailResponse of(
        PointCharges pointCharges, String type,
        String status, List<PointChargeReceiptResponse> receipt
    ) {
        return PointChargeDetailResponse.builder()
            .id(pointCharges.getId())
            .category(pointCharges.getPointCategory().getDescription())
            .type(type)
            .status(status)
            .trade(pointCharges.getChargePoint())
            .amount(pointCharges.getChargePoint())
            .receipt(receipt)
            .build();
    }
}
