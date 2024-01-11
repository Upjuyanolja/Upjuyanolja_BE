package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponAccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponPageResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponIssuanceRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.service.RoomService;
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
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;

    private final RoomService roomService;

    @Transactional(readOnly = true)
    public CouponPageResponse findCoupon(Pageable pageable) {
        List<CouponDetailResponse> response =
            couponRepository.findAll().stream().map(CouponDetailResponse::of).toList();

        return CouponPageResponse.of(
            new PageImpl<>(response, pageable, response.size())
        );
    }

    private List<CouponIssuance> getCouponRoomsByAccommodation(Long accommodationId) {
        List<CouponIssuance> couponIssuances =
            couponIssuanceRepository.findByAccommodationId(accommodationId)
                .orElseGet(ArrayList::new);
        return couponIssuances;
    }

    @Transactional(readOnly = true)
    public List<CouponAccommodationResponse> findCouponInAccommodation(Long accommodationId) {
        List<CouponIssuance> couponIssuances = getCouponRoomsByAccommodation(accommodationId);

        if (couponIssuances.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Coupon, List<String>> couponRoomMap = couponIssuances.stream()
            .collect(Collectors.groupingBy(
                    CouponIssuance::getCoupon,
                    Collectors.mapping(
                        couponRoom -> couponRoom.getRoom().getName(),
                        Collectors.toList()
                    )
                )
            );

        return couponRoomMap.entrySet().stream()
            .map(coupon -> CouponAccommodationResponse.from(coupon.getKey(), coupon.getValue()))
            .sorted(Comparator.comparing(CouponAccommodationResponse::id))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CouponRoomResponse> getSortedFlatCouponInAccommodation(Long accommodationId) {
        List<CouponRoomResponse> result = new ArrayList<>();
        Map<Long, TreeSet<Coupon>> couponRoomMap = getTypeCouponWithRoom(accommodationId,
            DiscountType.FLAT);

        if (couponRoomMap.isEmpty()) {
            return new ArrayList<>();
        }

        for (Entry<Long, TreeSet<Coupon>> roomCouponEntry : couponRoomMap.entrySet()) {
            Room room = roomService.findRoomById(roomCouponEntry.getKey());
            int price = room.getPrice().getOffWeekDaysMinFee();

            for (Coupon coupon : roomCouponEntry.getValue()) {
                result.add(CouponRoomResponse.from(coupon, DiscountType.getPaymentPrice(DiscountType.FLAT, price, coupon.getDiscount()));
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<CouponRoomResponse> getSortedRateCouponInAccommodation(
        Long accommodationId
    ) {
        List<CouponRoomResponse> result = new ArrayList<>();
        Map<Long, TreeSet<Coupon>> couponRoomMap = getTypeCouponWithRoom(
            accommodationId, DiscountType.RATE
        );

        if (couponRoomMap.isEmpty()) {
            return new ArrayList<>();
        }

        for (Entry<Long, TreeSet<Coupon>> roomCouponEntry : couponRoomMap.entrySet()) {
            Room room = roomService.findRoomById(roomCouponEntry.getKey());
            int price = room.getPrice().getOffWeekDaysMinFee();

            for (Coupon coupon : roomCouponEntry.getValue()) {
                result.add(CouponRoomResponse.from(
                        coupon, DiscountType.getPaymentPrice(DiscountType.RATE, price, coupon.getDiscount()
                    )
                );
            }
        }
        return result;
    }

    private Map<Long, TreeSet<Coupon>> getDiscountTypeCouponWithRoom(
        Long accommodationId, DiscountType discountType
    ) {
        List<CouponIssuance> couponIssuances = getCouponRoomsByAccommodation(accommodationId);
        Map<Long, TreeSet<Coupon>> couponRoomMap = new HashMap<>();

        if (couponIssuances.isEmpty()) {
            return Collections.emptyMap();
        }

        for (CouponIssuance couponIssuance : couponIssuances) {
            if (couponIssuance.getCoupon().getDiscountType() == type) {
                Long key = couponIssuance.getRoom().getId();
                Coupon value = couponIssuance.getCoupon();

                if (couponRoomMap.containsKey(key)) {
                    couponRoomMap.get(key).add(value);
                } else {
                    TreeSet<Coupon> coupons = new TreeSet<>
                        (Comparator.comparingInt(Coupon::getDiscount).reversed());
                    coupons.add(value);
                    couponRoomMap.put(key, coupons);
                }

            }
        }

        return couponRoomMap;
    }

    @Transactional(readOnly = true)
    public List<CouponRoomResponse> getSortedTotalCouponInRoom(
        Room room
    ) {
        List<CouponIssuance> couponIssuances = couponIssuanceRepository.findByRoom(room)
            .orElse(new ArrayList<>());
        TreeMap<Integer, Coupon> coupons = new TreeMap<>();

        if (couponIssuances.isEmpty()) {
            return new ArrayList<>();
        }

        for (CouponIssuance couponIssuance: couponIssuances) {
            DiscountType discountType = couponIssuance.getCoupon().getType();
            int roomPrice = room.getRoomPrice().getOffWeekDaysMinFee();
            int couponPrice = couponIssuance.getCoupon().getCouponPrice();

            /*여기 다시 리팩토링*/

            switch (discountType) {
                case FLAT:
                    coupons.put(roomPrice - couponPrice, couponRoom.getCoupon());
                    break;
                case RATE:
                    coupons.put((int) Math.round((1 - 0.01 * couponPrice) * roomPrice), couponRoom.getCoupon());
                    break;
            }
        }

        return coupons.entrySet().stream()
            .map(coupon -> CouponRoomResponse.from(coupon.getValue(),coupon.getKey()))
            .toList();
    }
}
