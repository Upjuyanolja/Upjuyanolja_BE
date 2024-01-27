package com.backoffice.upjuyanolja.domain.point.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PointUsageDetailReceiptResponse(
    String room,
    List<PointUsageCouponReceiptResponse> coupons

) {

    public static PointUsageDetailReceiptResponse of(
        String room, List<PointUsageCouponReceiptResponse> coupons
    ) {
        return PointUsageDetailReceiptResponse.builder()
            .room(room)
            .coupons(coupons)
            .build();
    }
}
