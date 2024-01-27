package com.backoffice.upjuyanolja.domain.point.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PointTotalDetailResponse(
    long id,
    String category,
    String type,
    String status,
    String name,
    String description,
    long trade,
    long amount,
    String date,
    Object receipt

) {

    public static PointTotalDetailResponse of(
        String category, String type, String status,
        String name, String description, long trade,
        long amount, String date, Object receipt
    ) {
        return PointTotalDetailResponse.builder()
            .category(category)
            .type(type)
            .status(status)
            .name(name)
            .description(description)
            .trade(trade)
            .amount(amount)
            .date(date)
            .receipt(receipt)
            .build();
    }

    public static PointTotalDetailResponse from(
        long id, PointTotalDetailResponse pointTotalDetailResponse
    ) {
        return PointTotalDetailResponse.builder()
            .id(id)
            .category(pointTotalDetailResponse.category)
            .type(pointTotalDetailResponse.type)
            .status(pointTotalDetailResponse.status)
            .name(pointTotalDetailResponse.name)
            .description(pointTotalDetailResponse.description)
            .trade(pointTotalDetailResponse.trade)
            .amount(pointTotalDetailResponse.amount)
            .date(pointTotalDetailResponse.date)
            .receipt(pointTotalDetailResponse.receipt)
            .build();
    }
}
