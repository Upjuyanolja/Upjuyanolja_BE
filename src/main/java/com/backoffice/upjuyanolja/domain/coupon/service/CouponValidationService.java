package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.exception.PointInsufficientException;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponValidationService {

    /**
     * 요청된 쿠폰의 검증 및 사용 가능 여부를 응답하는 Service
     */
    private final CouponRepository couponRepository;

    /**
     *
     * @param couponMakeRequest 쿠폰 등록 요청 Dto
     * @param currentMemberId   현재 접속한 회원(업주) id
     * @return
     */
    public boolean validCouponMakeRequest(
        final CouponMakeRequest couponMakeRequest, final Long currentMemberId
    ) {
        // 1. 업주의 id에 등록되어 있는 숙소가 있는지 1차 검증
        if (!couponRepository.existsAccommodationIdByMemberId(
            couponMakeRequest.accommodationId(), currentMemberId)) {
            throw new AccommodationNotFoundException();
        }

        // 2. 업주의 보유 포인트 검증
        final Integer ownerPoint = couponRepository.getOwnerPoint(currentMemberId);
        final int requestPoint = couponMakeRequest.totalPoints();
        if (ownerPoint == null || requestPoint < ownerPoint.intValue()) {
            throw new PointInsufficientException();
        }
        return true;
    }
}
