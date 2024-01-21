package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import lombok.Builder;

@Builder
public record CouponRoomsRequest(
    Long roomId,
    DiscountType discountType,
    int discount,
    int quantity,
    int eachPoint
) {
}
