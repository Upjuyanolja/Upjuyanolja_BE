package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CouponAddRequest(
    @NotNull(message = "숙소 식별자는 필수입니다.")
    Long accommodationId,
    @NotNull(message = "합계 포인트는 필수입니다.")
    long totalPoints,
    @NotNull(message = "쿠폰 노출 만료일자는 필수입니다.")
    LocalDate expiry,
    @NotNull(message = "객실 정보를 하나 이상 입력해 주세요.")
    List<CouponAddRooms> rooms
) {
}
