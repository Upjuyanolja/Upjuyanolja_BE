package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record CouponMakeViewResponse(
    Long accommodationId,
    String accommodationName,
    List<CouponRoomsResponse> rooms
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CouponMakeViewResponse that = (CouponMakeViewResponse) o;
        return Objects.equals(accommodationId, that.accommodationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accommodationId);
    }
}
