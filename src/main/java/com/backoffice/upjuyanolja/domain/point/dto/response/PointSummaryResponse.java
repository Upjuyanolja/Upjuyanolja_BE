package com.backoffice.upjuyanolja.domain.point.dto.response;

import lombok.Builder;

@Builder
public record PointSummaryResponse(
    long chargePoint,
    long usePoint,
    long currentPoint
) {

    public static PointSummaryResponse of(
        long chargePoint, long usePoint, long currentPoint
    ) {
        return PointSummaryResponse.builder()
            .chargePoint(chargePoint)
            .usePoint(usePoint)
            .currentPoint(currentPoint)
            .build();
    }
}
