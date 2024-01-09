package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponRoomRepository extends JpaRepository<CouponIssuance, Long> {

    @Query("select cr from CouponIssuance cr join cr.room r where r.accommodation.id = :accommodationId")
    Optional<List<CouponIssuance>> findByAccommodationId(Long accommodationId);
}
