package com.backoffice.upjuyanolja.domain.coupon.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record CouponMakeRequest(
    @NotNull(message = "숙소 식별자는 필수입니다.")
    Long accommodationId,
    @NotNull(message = "합계 금액은 필수입니다.")
    @Min(value = 1000, message = "합계 금액을 확인하여 주세요.")
    Integer totalPoints,
    @NotNull
    List<CouponRoomsRequest> rooms
) {

}
