package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {

    List<CouponIssuance> findByPointUsage(PointUsage pointUsage);

}
