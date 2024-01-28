package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.entity.RevenueTotal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueTotalRepository extends JpaRepository<RevenueTotal, Long> {
    Optional<RevenueTotal> findByAccommodationId(Long accommodationId);
}
