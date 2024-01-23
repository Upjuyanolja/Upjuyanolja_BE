package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CouponDeleteRequest(
    @NotNull
    Long accommodationId,
    @NotNull
    List<CouponDeleteRooms> rooms
) {
}
