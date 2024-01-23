package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import java.util.List;

public record CouponModifyRooms(
    Long roomId,
    List<CouponModifyInfos> coupons
) {
}
