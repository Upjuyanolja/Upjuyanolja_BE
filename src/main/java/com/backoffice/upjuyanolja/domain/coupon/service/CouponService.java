package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponAccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponShortResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<CouponAccommodationResponse> findCouponResponseInAccommodation(
        Long accommodationId) {
        List<CouponAccommodationResponse> responses = new ArrayList<>();
        List<Room> rooms = roomRepository.findAllByAccommodationId(accommodationId);

        for (Room room : rooms) {
            List<CouponShortResponse> coupons = couponRepository.findByRoom(room).stream()
                .filter(coupon -> coupon.getCouponStatus().equals(CouponStatus.ENABLE))
                .map(coupon -> CouponShortResponse.of(coupon))
                .toList();

            if (!coupons.isEmpty()) {
                responses.add(CouponAccommodationResponse.of(room, coupons));
            }
        }

        return responses;
    }

    public List<CouponDetailResponse> getSortedDiscountTypeCouponResponseInRoom(
        Room room, int roomPrice, List<Coupon> coupons, DiscountType discountType
    ) {
        List<CouponDetailResponse> responses = new ArrayList<>();
        List<Coupon> discountTypeCouponInRoom =
            getDiscountTypeCouponInRoom(coupons, discountType);
        List<Coupon> resultCoupons = new ArrayList<>();

        if (discountTypeCouponInRoom.isEmpty()) {
            return new ArrayList<>();
        }

        resultCoupons = sortDiscountTypeCouponInRoom(discountTypeCouponInRoom, roomPrice);

        for (Coupon coupon : resultCoupons) {
            responses.add(CouponDetailResponse.of(
                coupon,
                DiscountType.makePaymentPrice(coupon.getDiscountType(), roomPrice,
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

    @Transactional(readOnly = true)
    public List<CouponDetailResponse> getSortedTotalCouponResponseInRoom(Room room, int roomPrice) {
        List<Coupon> roomCoupons = couponRepository.findByRoom(room);

        if (roomCoupons.isEmpty()) {
            return new ArrayList<>();
        }

        roomCoupons.sort(Comparator.comparingInt(coupon -> DiscountType.makePaymentPrice(
                coupon.getDiscountType(), roomPrice, coupon.getDiscount()
            ))
        );

        return roomCoupons.stream()
            .filter(coupon -> coupon.getCouponStatus().equals(CouponStatus.ENABLE))
            .map(coupon -> {
                int discountedPrice = DiscountType.makePaymentPrice(
                    coupon.getDiscountType(), roomPrice, coupon.getDiscount()
                );
                return CouponDetailResponse.of(coupon, discountedPrice);
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String getDiscountTypeMainRoomCouponName(
        List<Room> rooms, DiscountType discountType, Map<Room, Integer> priceRoomMap
    ) {
        TreeMap<Integer, String> result = new TreeMap<>(Comparator.reverseOrder());

        for (Room room : rooms) {
            int roomPrice = priceRoomMap.get(room);
            List<Coupon> coupons = getDiscountTypeCouponInRoom(
                getCouponInRoom(room), discountType
            );

            if (coupons.isEmpty()) {
                continue;
            }

            coupons = sortDiscountTypeCouponInRoom(coupons, roomPrice);

            result.put(
                coupons.get(0).getDiscount(),
                DiscountType.makeDetailName(discountType, coupons.get(0).getDiscount())
            );

        }

        if (result.isEmpty()) {
            return "";
        }

        return result.firstEntry().getValue();
    }

    private List<Coupon> sortDiscountTypeCouponInRoom(
        List<Coupon> discountTypeCoupon, int roomPrice
    ) {
        List<Coupon> result = new ArrayList<>();
        result.addAll(discountTypeCoupon);

        Collections.sort(result,
            Comparator.comparingInt(coupon -> DiscountType.makePaymentPrice(
                    coupon.getDiscountType(), roomPrice, coupon.getDiscount()
                )
            )
        );

        return result;

    }

    private List<Coupon> getDiscountTypeCouponInRoom(
        List<Coupon> coupons, DiscountType discountType
    ) {
        return coupons.stream()
            .filter(coupon -> coupon.getDiscountType() == discountType &&
                coupon.getCouponStatus() == CouponStatus.ENABLE)
            .toList();
    }
}
