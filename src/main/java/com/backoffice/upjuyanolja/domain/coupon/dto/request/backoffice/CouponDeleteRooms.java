package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import java.util.List;

public record CouponDeleteRooms(
    Long roomId,
    List<CouponDeleteInfos> coupons
) {

}
