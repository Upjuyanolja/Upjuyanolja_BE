package com.backoffice.upjuyanolja.domain.reservation.service;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientCouponStockException;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidCouponException;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidReservationInfoException;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.global.annotation.ConcurrencyControl;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationStockService {

  private final RoomStockRepository roomStockRepository;
  private final CouponRepository couponRepository;

  @ConcurrencyControl(targetName = "roomStock", waitTime = 2, leaseTime = 1, timeUnit = TimeUnit.SECONDS)
  public void decreaseRoomStock(Long id, RoomStock roomStock) {
    try {
      roomStock.decrease(1);
    } catch (IllegalArgumentException e) {
      throw new InvalidReservationInfoException();
    }

    roomStockRepository.save(roomStock);
  }

  @ConcurrencyControl(targetName = "couponStock", waitTime = 2, leaseTime = 1, timeUnit = TimeUnit.SECONDS)
  public void decreaseCouponStock(Long id, Coupon coupon) {
    try {
      coupon.decreaseCouponStock(1);
    } catch (InsufficientCouponStockException e) {
      throw new InvalidCouponException();
    }

    couponRepository.save(coupon);
  }
}
