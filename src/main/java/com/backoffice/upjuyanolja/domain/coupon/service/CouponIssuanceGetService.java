package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponIssuanceRepository;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssuanceGetService {

    private final CouponIssuanceRepository couponIssuanceRepository;

    public List<CouponIssuance> getCouponIssuanceByPointUsage(PointUsage pointUsage) {
        return couponIssuanceRepository.findByPointUsage(pointUsage);
    }

}
