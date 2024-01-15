package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import java.util.List;
import lombok.Builder;

@Builder
public record CouponIssuanceResponse(
    Long id,
    String roomName,
    List<CouponDetailResponse> coupons
) {

    public static CouponIssuanceResponse of(
        CouponIssuance couponIssuance,
        List<CouponDetailResponse> coupons
    ) {
        return CouponIssuanceResponse.builder()
            .id(couponIssuance.getId())
            .roomName(couponIssuance.getRoom().getName())
            .coupons(coupons)
            .build();
    }

}