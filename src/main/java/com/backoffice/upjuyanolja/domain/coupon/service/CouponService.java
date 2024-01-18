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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    public List<CouponDetailResponse> getSortedCouponInAccommodation(
        Long accommodationId, DiscountType discountType
    ) {
        Map<Room, List<Coupon>> couponRoomMap = getDiscountTypeCouponInAccommodation(
            accommodationId, discountType
        );

        if (couponRoomMap.isEmpty()) {
            return new ArrayList<>();
        }

        return sortCouponInRoom(couponRoomMap);
    }

    private List<CouponDetailResponse> sortCouponInRoom(Map<Room, List<Coupon>> couponRoomMap) {
        List<CouponDetailResponse> responses = new ArrayList<>();

        for (Entry<Room, List<Coupon>> roomCouponEntry : couponRoomMap.entrySet()) {
            Room room = roomCouponEntry.getKey();
            int roomPrice = room.getPrice().getOffWeekDaysMinFee();

            roomCouponEntry.getValue()
                .sort(Comparator.comparingInt(coupon -> DiscountType.getPaymentPrice(
                        coupon.getDiscountType(), roomPrice, coupon.getDiscount()
                    ))
                );

            for (Coupon coupon : roomCouponEntry.getValue()) {
                responses.add(CouponDetailResponse.of(
                    coupon,
                    DiscountType.getPaymentPrice(coupon.getDiscountType(), roomPrice,
                        coupon.getDiscount()
                    ))
                );
            }
        }

        return responses;
    }

    private Map<Room, List<Coupon>> getDiscountTypeCouponInAccommodation(
        Long accommodationId, DiscountType discountType
    ) {
        List<Room> rooms = roomQueryUseCase.findByAccommodationId(accommodationId);
        Map<Room, List<Coupon>> couponRoomMap = new HashMap<>();

        for (Room room : rooms) {
            List<Coupon> value =
                couponRepository.findByRoomAndDiscountTypeOrderByDiscountDesc(room, discountType);

            if (value.isEmpty()) {
                continue;
            }

            couponRoomMap.put(room, value);
        }

        return couponRoomMap;
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
