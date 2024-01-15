package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponBackofficeService {

    /**
     * 쿠폰 데이터 Response, Read 만 담당하는 Service
     */
    private final CouponRepository couponRepository;

    public CouponMakeViewResponse getRoomsByAccommodation(Long accommodationId) {
        return couponRepository.findRoomsByAccommodationId(accommodationId);
    }

}
