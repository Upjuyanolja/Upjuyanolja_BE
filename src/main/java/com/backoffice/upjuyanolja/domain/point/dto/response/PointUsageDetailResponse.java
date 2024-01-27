package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import lombok.Builder;

@Builder
public record PointUsageDetailResponse(
    long id,
    String category,
    String type,
    String status,
    String name,
    long trade,
    long amount,
    PointUsageReceiptResponse receipt

) {

    public static PointUsageDetailResponse of(
        PointCharges pointCharges, String category, String type,
        PointUsageReceiptResponse receipt
    ) {
        return PointUsageDetailResponse.builder()
            .id(pointCharges.getId())
            .category(category)
            .type(type)
            .status(pointCharges.getPointStatus().getDescription())
            .name("할인 쿠폰 구매")
            .trade(pointCharges.getChargePoint())
            .amount(pointCharges.getChargePoint())
            .receipt(receipt)
            .build();
    }
}
