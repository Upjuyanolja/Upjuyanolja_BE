package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CouponManageResponse(
    Long accommodationId,
    String accommodationName,
    LocalDate expiry,
    List<CouponManageRooms> rooms
) {
}
