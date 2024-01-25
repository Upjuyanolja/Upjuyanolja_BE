package com.backoffice.upjuyanolja.domain.point.dto.response;

import lombok.Builder;

@Builder
public record PointTotalBalanceResponse(
    long totalPoint
) {

    public static PointTotalBalanceResponse of(
        long totalPoint
    ) {
        return PointTotalBalanceResponse.builder()
            .totalPoint(totalPoint)
            .build();
    }
}
