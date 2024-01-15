package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponAccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponIssuanceRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final RoomQueryUseCase roomQueryUseCase;

    @Transactional(readOnly = true)
    public List<CouponAccommodationResponse> findCouponInAccommodation(Long accommodationId) {
        List<CouponIssuance> couponIssuances = getCouponIssaunceByAccommodation(accommodationId);

        if (couponIssuances.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Coupon, List<String>> couponIssuanceMap = couponIssuances.stream()
            .collect(Collectors.groupingBy(
                    CouponIssuance::getCoupon,
                    Collectors.mapping(
                        couponIssuance -> couponIssuance.getRoom().getName(),
                        Collectors.toList()
                    )
                )
            );

        return couponIssuanceMap.entrySet().stream()
            .map(coupon -> CouponAccommodationResponse.of(coupon.getKey(), coupon.getValue()))
            .sorted(Comparator.comparing(CouponAccommodationResponse::id))
            .toList();
    }

    private List<CouponIssuance> getCouponIssaunceByAccommodation(Long accommodationId) {
        List<CouponIssuance> couponIssuances =
            couponIssuanceRepository.findByAccommodationId(accommodationId)
                .orElseGet(ArrayList::new);
        return couponIssuances;
    }

    @Transactional(readOnly = true)
    public List<CouponDetailResponse> getSortedFlatCouponInAccommodation(Long accommodationId) {
        List<CouponDetailResponse> result = new ArrayList<>();
        Map<Long, TreeSet<Coupon>> couponIssuanceMap = getDiscountTypeCouponWithRoom(
            accommodationId,
            DiscountType.FLAT);

        if (couponIssuanceMap.isEmpty()) {
            return new ArrayList<>();
        }

        for (Entry<Long, TreeSet<Coupon>> roomCouponEntry : couponIssuanceMap.entrySet()) {
            Room room = roomQueryUseCase.findRoomById(roomCouponEntry.getKey());
            int price = room.getPrice().getOffWeekDaysMinFee();

            for (Coupon coupon : roomCouponEntry.getValue()) {
                result.add(CouponDetailResponse.of(coupon,
                        DiscountType.getPaymentPrice(DiscountType.FLAT, price, coupon.getDiscount())
                    )
                );
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<CouponDetailResponse> getSortedRateCouponInAccommodation(
        Long accommodationId
    ) {
        List<CouponDetailResponse> result = new ArrayList<>();
        Map<Long, TreeSet<Coupon>> couponIssuanceMap = getDiscountTypeCouponWithRoom(
            accommodationId, DiscountType.RATE
        );

        if (couponIssuanceMap.isEmpty()) {
            return new ArrayList<>();
        }

        for (Entry<Long, TreeSet<Coupon>> roomCouponEntry : couponIssuanceMap.entrySet()) {
            Room room = roomQueryUseCase.findRoomById(roomCouponEntry.getKey());
            int price = room.getPrice().getOffWeekDaysMinFee();

            for (Coupon coupon : roomCouponEntry.getValue()) {
                result.add(CouponDetailResponse.of(
                    coupon,
                    DiscountType.getPaymentPrice(DiscountType.RATE, price, coupon.getDiscount()
                    ))
                );
            }
        }
        return result;
    }

    private Map<Long, TreeSet<Coupon>> getDiscountTypeCouponWithRoom(
        Long accommodationId, DiscountType discountType
    ) {
        List<CouponIssuance> couponIssuances = getCouponIssaunceByAccommodation(accommodationId);
        Map<Long, TreeSet<Coupon>> couponIssuanceMap = new HashMap<>();

        if (couponIssuances.isEmpty()) {
            return Collections.emptyMap();
        }

        for (CouponIssuance couponIssuance : couponIssuances) {
            if (couponIssuance.getCoupon().getDiscountType() == discountType) {
                Long key = couponIssuance.getRoom().getId();
                Coupon value = couponIssuance.getCoupon();

                if (couponIssuanceMap.containsKey(key)) {
                    couponIssuanceMap.get(key).add(value);
                } else {
                    TreeSet<Coupon> coupons = new TreeSet<>
                        (Comparator.comparingInt(Coupon::getDiscount).reversed());
                    coupons.add(value);
                    couponIssuanceMap.put(key, coupons);
                }

            }
        }

        return couponIssuanceMap;
    }

    @Transactional(readOnly = true)
    public List<CouponDetailResponse> getSortedTotalCouponInRoom(
        Room room
    ) {
        List<CouponIssuance> couponIssuances = couponIssuanceRepository.findByRoom(room)
            .orElse(new ArrayList<>());
        TreeMap<Integer, Coupon> coupons = new TreeMap<>();

        if (couponIssuances.isEmpty()) {
            return new ArrayList<>();
        }

        for (CouponIssuance couponIssuance : couponIssuances) {
            DiscountType discountType = couponIssuance.getCoupon().getDiscountType();
            int roomPrice = room.getPrice().getOffWeekDaysMinFee();
            int couponPrice = couponIssuance.getCoupon().getDiscount();

            coupons.put(
                DiscountType.getPaymentPrice(discountType, roomPrice, couponPrice),
                couponIssuance.getCoupon()
            );
        }

        return coupons.entrySet().stream()
            .map(coupon -> CouponDetailResponse.of(coupon.getValue(), coupon.getKey()))
            .toList();
    }
}
