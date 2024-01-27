package com.backoffice.upjuyanolja.domain.point.dto.response;

import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import lombok.Builder;

@Builder
public record PointUsageDetailResponse(
    long id,
    String category,
    String type,
    String status,
    String name,
    String description,
    long trade,
    long amount,
    PointUsageReceiptResponse receipt

) {

    public static PointUsageDetailResponse of(
        PointUsage pointUsage, String description,
        long trade, long amount, PointUsageReceiptResponse receipt
    ) {
        return PointUsageDetailResponse.builder()
            .id(pointUsage.getId())
            .category("사용")
            .type("쿠폰")
            .status("구매 확정")
            .name("할인 쿠폰 구매")
            .description(description)
            .trade(trade)
            .amount(amount)
            .receipt(receipt)
            .build();
    }
}
