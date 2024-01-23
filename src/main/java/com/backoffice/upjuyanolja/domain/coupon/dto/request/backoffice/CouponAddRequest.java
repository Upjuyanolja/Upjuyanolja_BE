package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CouponAddRequest(
    @NotNull
    Long accommodationId,
    @NotNull
    int totalPoints,
    @NotNull
    LocalDate expiry,
    @NotNull
    List<CouponAddRooms> rooms
) {
}
