package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomsInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomOptionNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomPriceNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomStockNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomOptionRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomPriceRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomQueryService implements RoomQueryUseCase {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;
    private final RoomRepository roomRepository;
    private final RoomOptionRepository roomOptionRepository;
    private final RoomPriceRepository roomPriceRepository;
    private final RoomStockRepository roomStockRepository;

    private final MemberGetService memberGetService;
    private final CouponService couponService;

    @Override
    public List<Room> findByAccommodationId(long accommodationId) {
        return roomRepository.findByAccommodationId(accommodationId);
    }

    @Override
    public Room findRoomById(long roomId) {
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
    }

    @Override
    public RoomOption findRoomOptionByRoom(Room room) {
        return roomOptionRepository.findByRoom(room)
            .orElseThrow(RoomOptionNotFoundException::new);
    }

    @Override
    public RoomPrice findRoomPriceByRoom(Room room) {
        return roomPriceRepository.findByRoom(room)
            .orElseThrow(RoomPriceNotFoundException::new);
    }

    @Override
    public RoomPageResponse getRooms(long memberId, long accommodationId, Pageable pageable) {
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
        checkOwnership(member, accommodation);
        List<RoomsInfoResponse> rooms = new ArrayList<>();
        Page<Room> roomPage = roomRepository.findAllByAccommodation(accommodationId, pageable);
        roomPage.get().forEach(room -> {
            List<CouponDetailResponse> couponDetails = new ArrayList<>();
            List<Coupon> coupons = couponService.getCouponInRoom(room);
            RoomOption option = findRoomOptionByRoom(room);
            int roomPrice = findRoomPriceByRoom(room).getOffWeekDaysMinFee();

            couponDetails.addAll(couponService
                .getSortedDiscountTypeCouponResponseInRoom(
                    room, roomPrice,
                    coupons, DiscountType.FLAT)
            );
            couponDetails.addAll(couponService
                .getSortedDiscountTypeCouponResponseInRoom(
                    room, roomPrice,
                    coupons, DiscountType.RATE)
            );

            rooms.add(RoomsInfoResponse.of(room, option, couponDetails,
                roomPrice));
        });
        return RoomPageResponse.builder()
            .pageNum(roomPage.getNumber())
            .pageSize(roomPage.getSize())
            .totalPages(roomPage.getTotalPages())
            .totalElements(roomPage.getTotalElements())
            .isLast(roomPage.isLast())
            .rooms(rooms)
            .build();
    }

    @Override
    public RoomInfoResponse getRoom(long memberId, long roomId) {
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        checkOwnership(member, room.getAccommodation());

        RoomOption option = findRoomOptionByRoom(room);
        int roomPrice = findRoomPriceByRoom(room).getOffWeekDaysMinFee();

        return RoomInfoResponse.of(room, option, roomPrice);
    }

    @Override
    public List<RoomStock> getFilteredRoomStocksByDate(
        Room room, LocalDate startDate, LocalDate endDate
    ) {
        return roomStockRepository.findByRoom(room)
            .orElseThrow(RoomStockNotFoundException::new)
            .stream()
            .filter(
                stock ->
                    !(stock.getDate().isBefore(startDate)) &&
                        !(stock.getDate().isAfter(endDate))
            )
            .toList();
    }

    @Override
    public Map<Room, Integer> getMinRoomPriceWithRoom(List<Room> rooms) {
        Map<Room, Integer> roomMinPriceMap = new HashMap<>();

        for (Room room: rooms) {
            int roomPrice = findRoomPriceByRoom(room).getOffWeekDaysMinFee();
            roomMinPriceMap.put(room, roomPrice);
        }

        return roomMinPriceMap;
    }

    private void checkOwnership(Member member, Accommodation accommodation) {
        if (!accommodationOwnershipRepository
            .existsAccommodationOwnershipByMemberAndAccommodation(member, accommodation)) {
            throw new NotOwnerException();
        }
    }

}
