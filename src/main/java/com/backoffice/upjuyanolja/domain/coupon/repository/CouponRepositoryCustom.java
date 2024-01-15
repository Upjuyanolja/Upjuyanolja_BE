package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;

public interface CouponRepositoryCustom {

    CouponMakeViewResponse findRoomsByAccommodationId(Long accommodationId);

    boolean existsAccommodationIdByMemberId(Long accommodationId, Long memberId);

    Integer getOwnerPoint(Long memberId);

    boolean existsPointIdByMemberId(Long memberId);

    Integer findByPresentRoomId(Long roomId, int discount, DiscountType discountType);

}
