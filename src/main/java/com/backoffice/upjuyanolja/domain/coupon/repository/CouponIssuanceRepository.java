package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {

    @Query("select ci from CouponIssuance ci join ci.room r where r.accommodation.id = :accommodationId")
    Optional<List<CouponIssuance>> findByAccommodationId(Long accommodationId);

    Optional<List<CouponIssuance>> findByRoom(Room room);
}