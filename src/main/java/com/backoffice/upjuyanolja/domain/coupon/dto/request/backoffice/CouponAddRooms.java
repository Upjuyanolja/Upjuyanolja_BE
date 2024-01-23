package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import java.util.List;

public record CouponAddRooms(
    Long roomId,
    List<CouponAddInfos> coupons
) {
}
