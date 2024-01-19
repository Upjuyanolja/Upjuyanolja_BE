package com.backoffice.upjuyanolja.domain.reservation.service;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientCouponStockException;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidCouponException;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidReservationInfoException;
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationException;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.global.concurrency.annotation.ConcurrencyControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationStockService {

    private final RoomStockRepository roomStockRepository;
    private final CouponRepository couponRepository;

    @ConcurrencyControl(lockName = "roomStock")
    public RoomStock increaseRoomStock(Long id) {
        RoomStock roomStock = roomStockRepository.findById(id)
            .orElseThrow(NoSuchReservationException::new);
        roomStock.increase(1);
        return roomStockRepository.save(roomStock);
    }

    @ConcurrencyControl(lockName = "roomStock")
    public RoomStock decreaseRoomStock(Long id) {
        try {
            RoomStock roomStock = roomStockRepository.findById(id)
                .orElseThrow(InvalidReservationInfoException::new);
            roomStock.decrease(1);
            return roomStockRepository.save(roomStock);
        } catch (IllegalArgumentException e) {
            throw new InvalidReservationInfoException();
        }
    }

    @ConcurrencyControl(lockName = "couponStock")
    public Coupon increaseCouponStock(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(NoSuchReservationException::new);
        coupon.increaseCouponStock(1);
        return couponRepository.save(coupon);
    }

    @ConcurrencyControl(lockName = "couponStock")
    public Coupon decreaseCouponStock(Long id) {
        try {
            Coupon coupon = couponRepository.findById(id).orElseThrow(InvalidCouponException::new);
            coupon.decreaseCouponStock(1);
            return couponRepository.save(coupon);
        } catch (InsufficientCouponStockException e) {
            throw new InvalidCouponException();
        }
    }
}
