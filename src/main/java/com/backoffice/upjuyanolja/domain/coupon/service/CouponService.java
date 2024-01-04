package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponAccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponPageResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponRoom;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType.Type;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRoomRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.service.RoomService;
import com.querydsl.core.util.StringUtils;
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
    private final CouponRoomRepository couponRoomRepository;

    private final RoomService roomService;

    @Transactional(readOnly = true)
    public CouponPageResponse findCoupon(Pageable pageable) {
        List<CouponDetailResponse> response =
            couponRepository.findAll().stream().map(CouponDetailResponse::from).toList();

        return CouponPageResponse.from(
            new PageImpl<>(response, pageable, response.size())
        );
    }

    private List<CouponRoom> getCouponRoomsByAccommodation(Long accommodationId) {
        List<CouponRoom> couponRooms =
            couponRoomRepository.findByAccommodationId(accommodationId).orElseGet(ArrayList::new);
        return couponRooms;
    }

    @Transactional(readOnly = true)
    public List<CouponAccommodationResponse> findCouponInAccommodation(Long accommodationId) {
        List<CouponRoom> couponRooms = getCouponRoomsByAccommodation(accommodationId);

        if (couponRooms.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Coupon, List<String>> couponRoomMap = couponRooms.stream()
            .collect(Collectors.groupingBy(
                    CouponRoom::getCoupon,
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
            Type.FLAT);

        if (couponRoomMap.isEmpty()) {
            return new ArrayList<>();
        }

        for (Entry<Long, TreeSet<Coupon>> roomCouponEntry : couponRoomMap.entrySet()) {
            Room room = roomService.findRoomById(roomCouponEntry.getKey());
            int roomPrice = room.getRoomPrice().getOffWeekDaysMinFee();

            for (Coupon coupon : roomCouponEntry.getValue()) {
                result.add(CouponRoomResponse.from(coupon, roomPrice - coupon.getCouponPrice()));
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<CouponRoomResponse> getSortedPercentageCouponInAccommodation(
        Long accommodationId
    ) {
        List<CouponRoomResponse> result = new ArrayList<>();
        Map<Long, TreeSet<Coupon>> couponRoomMap = getTypeCouponWithRoom(
            accommodationId, Type.PERCENTAGE
        );

        if (couponRoomMap.isEmpty()) {
            return new ArrayList<>();
        }

        for (Entry<Long, TreeSet<Coupon>> roomCouponEntry : couponRoomMap.entrySet()) {
            Room room = roomService.findRoomById(roomCouponEntry.getKey());
            int roomPrice = room.getRoomPrice().getOffWeekDaysMinFee();

            for (Coupon coupon : roomCouponEntry.getValue()) {
                result.add(CouponRoomResponse.from(
                        coupon, (int) Math.round((1 - 0.01 * coupon.getCouponPrice()) * roomPrice)
                    )
                );
            }
        }
        return result;
    }

    private Map<Long, TreeSet<Coupon>> getTypeCouponWithRoom(
        Long accommodationId, Type type
    ) {
        List<CouponRoom> couponRooms = getCouponRoomsByAccommodation(accommodationId);
        Map<Long, TreeSet<Coupon>> couponRoomMap = new HashMap<>();

        if (couponRooms.isEmpty()) {
            return Collections.emptyMap();
        }

        for (CouponRoom couponRoom : couponRooms) {
            if (couponRoom.getCoupon().getType() == type) {
                Long key = couponRoom.getRoom().getId();
                Coupon value = couponRoom.getCoupon();

                if (couponRoomMap.containsKey(key)) {
                    couponRoomMap.get(key).add(value);
                } else {
                    TreeSet<Coupon> coupons = new TreeSet<>
                        (Comparator.comparingInt(Coupon::getCouponPrice).reversed());
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
        List<CouponRoom> couponRooms = couponRoomRepository.findByRoom(room)
            .orElse(new ArrayList<>());
        TreeMap<Integer, Coupon> coupons = new TreeMap<>();

        if (couponRooms.isEmpty()) {
            return new ArrayList<>();
        }

        for (CouponRoom couponRoom: couponRooms) {
            Type type = couponRoom.getCoupon().getType();
            int roomPrice = room.getRoomPrice().getOffWeekDaysMinFee();
            int couponPrice = couponRoom.getCoupon().getCouponPrice();

            switch (type) {
                case FLAT:
                    coupons.put(roomPrice - couponPrice, couponRoom.getCoupon());
                    break;
                case PERCENTAGE:
                    coupons.put((int) Math.round((1 - 0.01 * couponPrice) * roomPrice), couponRoom.getCoupon());
                    break;
            }
        }

        return coupons.entrySet().stream()
            .map(coupon -> CouponRoomResponse.from(coupon.getValue(),coupon.getKey()))
            .toList();
    }

}
