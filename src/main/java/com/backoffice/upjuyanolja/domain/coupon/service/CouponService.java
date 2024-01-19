package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponAccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponShortResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final RoomQueryUseCase roomQueryUseCase;

    @Transactional(readOnly = true)
    public List<CouponAccommodationResponse> findCouponInAccommodation(Long accommodationId) {
        List<CouponAccommodationResponse> responses = new ArrayList<>();
        List<Room> rooms = roomQueryUseCase.findByAccommodationId(accommodationId);

        for (Room room : rooms) {
            List<CouponShortResponse> coupons = couponRepository.findByRoom(room).stream()
                .map(coupon -> CouponShortResponse.of(coupon))
                .toList();

            responses.add(CouponAccommodationResponse.of(room, coupons));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public List<CouponDetailResponse> getSortedCouponInRoom(
        Room room, List<Coupon> coupons, DiscountType discountType
    ) {
        List<CouponDetailResponse> responses = new ArrayList<>();
        int roomPrice = room.getPrice().getOffWeekDaysMinFee();
        List<Coupon> resultCoupons = getDiscountTypeCouponInRoom(coupons, discountType);

        if (resultCoupons.isEmpty()) {
            return new ArrayList<>();
        }

        Collections.sort(coupons, Comparator.comparingInt(coupon -> DiscountType.getPaymentPrice(
                coupon.getDiscountType(), roomPrice, coupon.getDiscount()
            ))
        );

        for (Coupon coupon : coupons) {
            responses.add(CouponDetailResponse.of(
                coupon,
                DiscountType.getPaymentPrice(coupon.getDiscountType(), roomPrice,
                    coupon.getDiscount()
                ))
            );
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public List<Coupon> getCouponInRoom(Room room) {
        return couponRepository.findByRoom(room);
    }

    private List<Coupon> getDiscountTypeCouponInRoom(
        List<Coupon> coupons, DiscountType discountType
    ) {
        return coupons.stream()
            .filter(coupon -> coupon.getDiscountType() == discountType)
            .sorted(Comparator.comparingInt(Coupon::getDiscount).reversed())
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CouponDetailResponse> getSortedTotalCouponInRoom(Room room) {
        List<Coupon> roomCoupons = couponRepository.findByRoom(room);
        int roomPrice = room.getPrice().getOffWeekDaysMinFee();

        if (roomCoupons.isEmpty()) {
            return new ArrayList<>();
        }

        roomCoupons.sort(Comparator.comparingInt(coupon -> DiscountType.getPaymentPrice(
                coupon.getDiscountType(), roomPrice, coupon.getDiscount()
            ))
        );

        return roomCoupons.stream()
            .map(coupon -> {
                int discountedPrice = DiscountType.getPaymentPrice(
                    coupon.getDiscountType(), roomPrice, coupon.getDiscount()
                );
                return CouponDetailResponse.of(coupon, discountedPrice);
            })
            .collect(Collectors.toList());
    }
}
