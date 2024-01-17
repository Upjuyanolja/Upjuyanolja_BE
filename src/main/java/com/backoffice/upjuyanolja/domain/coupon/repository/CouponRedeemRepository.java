package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponRedeem;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedeemRepository extends JpaRepository<CouponRedeem, Long> {

    Optional<CouponRedeem> findByReservation(Reservation reservation);
}
