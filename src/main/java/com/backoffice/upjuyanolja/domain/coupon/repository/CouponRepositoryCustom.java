package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeQueryDto;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageQueryDto;
import java.util.List;

public interface CouponRepositoryCustom {

    List<CouponMakeQueryDto> findRoomsByAccommodationId(Long accommodationId);

    boolean existsAccommodationIdByMemberId(Long accommodationId, Long memberId);

    List<CouponManageQueryDto> findCouponsByAccommodationId(Long accommodationId);

}
