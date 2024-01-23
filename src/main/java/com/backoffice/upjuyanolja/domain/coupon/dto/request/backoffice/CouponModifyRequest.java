package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CouponModifyRequest(
    @NotNull
    Long accommodationId,
    @NotNull
    LocalDate expiry,
    @NotNull
    List<CouponModifyRooms> rooms
) {
}
