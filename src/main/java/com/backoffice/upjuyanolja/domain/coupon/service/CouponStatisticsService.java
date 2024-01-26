package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.CouponStatisticsResponse;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponStatisticsService {

    private final CouponStatisticsRepository couponStatisticsRepository;

    public CouponStatisticsResponse getCouponStatistics(Long accommodationId) {
        CouponStatistics couponStatistics = couponStatisticsRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
        return CouponStatisticsResponse.from(couponStatistics);
    }
}
