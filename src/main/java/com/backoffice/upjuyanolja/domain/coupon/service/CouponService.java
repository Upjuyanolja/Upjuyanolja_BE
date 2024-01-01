package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Room;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponAccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponPageResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponRoom;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType.Type;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRoomRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    public CouponPageResponse findCoupon(Pageable pageable) {
        List<CouponDetailResponse> response =
            couponRepository.findAll().stream().map(CouponDetailResponse::from).toList();

        return CouponPageResponse.from(
            new PageImpl<>(response, pageable, response.size())
        );
    }

    @Transactional(readOnly = true)
    public List<CouponAccommodationResponse> findCouponInAccommodation(Long accommodationId) {
        List<CouponRoom> couponRooms = getCouponRoomsByAccommodation(accommodationId);

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

    private List<CouponRoom> getCouponRoomsByAccommodation(Long accommodationId) {
        List<CouponRoom> couponRooms =
            couponRoomRepository.findByAccommodationId(accommodationId).orElseGet(ArrayList::new);
        return couponRooms;
    }

    public List<CouponRoomDetailResponse> getSortedFlatCouponInAccommodation(
        Accommodation accommodation) {
        List<CouponRoomDetailResponse> result = new ArrayList<>();
        Map<Room, TreeSet<Coupon>> couponRoomMap = getTypeCouponWithRoom(accommodation, Type.FLAT);

        for (Entry<Room, TreeSet<Coupon>> roomCouponEntry : couponRoomMap.entrySet()) {
            Room room = roomCouponEntry.getKey();
            List<CouponRoomResponse> responses = new ArrayList<>();
            int roomPrice = room.getRoomPrice().getOffWeekDaysMinFee();

            roomCouponEntry.getValue().stream()
                .map(coupon ->
                    responses.add(
                        CouponRoomResponse.from(coupon, roomPrice - coupon.getCouponPrice())
                    )
                );
            result.add(CouponRoomDetailResponse.from(room.getName(), responses));
        }
        return result;
    }

    public List<CouponRoomDetailResponse> getSortedPercentageCouponInAccommodation(
        Accommodation accommodation) {
        List<CouponRoomDetailResponse> result = new ArrayList<>();
        Map<Room, TreeSet<Coupon>> couponRoomMap = getTypeCouponWithRoom(accommodation,
            Type.PERCENTAGE);

        for (Entry<Room, TreeSet<Coupon>> roomCouponEntry : couponRoomMap.entrySet()) {
            Room room = roomCouponEntry.getKey();
            List<CouponRoomResponse> responses = new ArrayList<>();
            int roomPrice = room.getRoomPrice().getOffWeekDaysMinFee();

            roomCouponEntry.getValue().stream()
                .map(coupon ->
                    responses.add(
                        CouponRoomResponse.from(coupon,
                            (int) Math.round((1 - 0.01 * coupon.getCouponPrice()) * roomPrice)
                        )
                    )
                );
            result.add(CouponRoomDetailResponse.from(room.getName(), responses));
        }
        return result;
    }

    private Map<Room, TreeSet<Coupon>> getTypeCouponWithRoom(Accommodation accommodation,
        Type type) {
        List<CouponRoom> couponRooms = getCouponRoomsByAccommodation(accommodation.getId());

        Map<Room, TreeSet<Coupon>> couponRoomMap = couponRooms.stream()
            .filter(couponRoom -> couponRoom.getCoupon().getType() == type)
            .collect(Collectors.groupingBy(
                    CouponRoom::getRoom,
                    Collectors.mapping(
                        couponRoom -> couponRoom.getCoupon(),
                        Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparing(Coupon::getCouponPrice).reversed())
                        )
                    )
                )
            );
        return couponRoomMap;
    }
}
