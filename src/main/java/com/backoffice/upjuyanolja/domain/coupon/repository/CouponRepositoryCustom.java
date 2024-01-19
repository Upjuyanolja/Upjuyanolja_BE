package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import java.util.Optional;

public interface CouponRepositoryCustom {

    CouponMakeViewResponse findRoomsIdByAccommodationId(Long accommodationId);

    boolean existsAccommodationIdByMemberId(Long accommodationId, Long memberId);

}
