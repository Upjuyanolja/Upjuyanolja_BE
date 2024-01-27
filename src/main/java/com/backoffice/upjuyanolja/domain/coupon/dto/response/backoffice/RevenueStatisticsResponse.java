package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record RevenueStatisticsResponse(
    Long accommodationId,
    List<RevenueInfo> revenue,
    String couponMessage
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RevenueStatisticsResponse that = (RevenueStatisticsResponse) o;
        return Objects.equals(accommodationId, that.accommodationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accommodationId);
    }
}
