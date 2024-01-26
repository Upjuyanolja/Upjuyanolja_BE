package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageAddRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageDeleteRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomsInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.exception.DuplicateRoomNameException;
import com.backoffice.upjuyanolja.domain.room.exception.InvalidRoomStatusException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomImageNotExistsException;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import com.backoffice.upjuyanolja.global.util.DateTimeParser;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomCommandService implements RoomCommandUseCase {

    private final MemberGetService memberGetService;
    private final RoomQueryUseCase roomQueryUseCase;
    private final AccommodationQueryUseCase accommodationQueryUseCase;
    private final EntityManager em;
    private final CouponService couponService;

    @Override
    public RoomInfoResponse registerRoom(
        long memberId,
        long accommodationId,
        RoomRegisterRequest request
    ) {
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = accommodationQueryUseCase
            .getAccommodationById(accommodationId);
        checkOwnership(member, accommodation);

        return saveRoom(accommodation, request);
    }

    @Override
    public RoomInfoResponse saveRoom(Accommodation accommodation, RoomRegisterRequest request) {
        validateRoomName(request.name(), accommodation);
        if (request.images().isEmpty()) {
            throw new RoomImageNotExistsException();
        }

        Room room = roomQueryUseCase.saveRoom(accommodation, Room.builder()
            .accommodation(accommodation)
            .name(request.name())
            .status(RoomStatus.SELLING)
            .price(RoomPrice.builder()
                .offWeekDaysMinFee(request.price())
                .offWeekendMinFee(request.price())
                .peakWeekDaysMinFee(request.price())
                .peakWeekendMinFee(request.price())
                .build())
            .defaultCapacity(request.defaultCapacity())
            .maxCapacity(request.maxCapacity())
            .checkInTime(DateTimeParser.timeParser(request.checkInTime()))
            .checkOutTime(DateTimeParser.timeParser(request.checkOutTime()))
            .amount(request.amount())
            .images(new ArrayList<>())
            .option(RoomOptionRequest.toEntity(request.option()))
            .build()
        );
        // 저장한 객실 이미지를 응답하기 위한 임시 방편
        roomQueryUseCase.saveRoomImages(RoomImageRequest.toEntity(room, request.images()))
            .forEach(roomImage -> room.getImages().add(roomImage));
//        em.refresh(room);

        createRoomStock(room);

        return RoomInfoResponse.of(room);
    }

    @Override
    public RoomPageResponse getRooms(long memberId, long accommodationId, Pageable pageable) {
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = accommodationQueryUseCase
            .getAccommodationById(accommodationId);
        checkOwnership(member, accommodation);
        List<RoomsInfoResponse> rooms = new ArrayList<>();
        Page<Room> roomPage = roomQueryUseCase.findAllByAccommodationId(accommodationId, pageable);
        roomPage.get().forEach(room -> {
            List<CouponDetailResponse> couponDetails = new ArrayList<>();
            List<Coupon> coupons = couponService.getCouponInRoom(room);
            couponDetails.addAll(couponService
                .getSortedCouponResponseInRoom(room, coupons, DiscountType.FLAT));
            couponDetails.addAll(couponService
                .getSortedCouponResponseInRoom(room, coupons, DiscountType.RATE));
            rooms.add(RoomsInfoResponse.of(room, couponDetails));
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
        Room room = roomQueryUseCase.findRoomById(roomId);
        checkOwnership(member, room.getAccommodation());
        return RoomInfoResponse.of(room);
    }

    @Override
    public RoomInfoResponse modifyRoom(long memberId, long roomId, RoomUpdateRequest request) {
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomQueryUseCase.findRoomById(roomId);

        if (!room.getName().equals(request.name())) {
            validateRoomName(request.name(), room.getAccommodation());
        }
        validateRoomStatus(request.status());
        checkOwnership(member, room.getAccommodation());
        updateRoom(room, request);
        em.flush();
        em.refresh(room);
        return RoomInfoResponse.of(room);
    }

    @Override
    public List<RoomStock> getFilteredRoomStocksByDate(
        Room room, LocalDate startDate, LocalDate endDate
    ) {
        return roomQueryUseCase.findStockByRoom(room).stream()
            .filter(
                stock ->
                    !(stock.getDate().isBefore(startDate)) &&
                        !(stock.getDate().isAfter(endDate))
            )
            .toList();
    }

    @Override
    public RoomInfoResponse deleteRoom(long memberId, long roomId) {
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomQueryUseCase.findRoomById(roomId);
        checkOwnership(member, room.getAccommodation());
        room.delete(LocalDateTime.now());
        return RoomInfoResponse.of(room);
    }

    private void validateRoomName(String name, Accommodation accommodation) {
        if (roomQueryUseCase.existsRoomByNameAndAccommodation(name, accommodation)) {
            throw new DuplicateRoomNameException();
        }
    }

    private void checkOwnership(Member member, Accommodation accommodation) {
        if (!accommodationQueryUseCase
            .existsOwnershipByMemberAndAccommodation(member, accommodation)) {
            throw new NotOwnerException();
        }
    }

    private void updateRoom(Room room, RoomUpdateRequest request) {
        if (room.getAmount() != request.amount()) {
            updateRoomStock(room, request.amount() - room.getAmount());
        }
        room.updateRoom(request.toRoomUpdateDto());
        addRoomImages(room, request.addImages());
        roomQueryUseCase.deleteRoomImages(getRoomImages(request.deleteImages()));
    }

    private void updateRoomStock(Room room, int quantity) {
        List<RoomStock> stocks = roomQueryUseCase
            .findStocksByRoomAndDateAfter(room, LocalDate.now().minusDays(1));
        stocks.forEach(stock -> stock.update(quantity));
    }

    private void addRoomImages(Room room, List<RoomImageAddRequest> requests) {
        List<RoomImage> images = new ArrayList<>();
        for (RoomImageAddRequest request : requests) {
            images.add(RoomImage.builder()
                .room(room)
                .url(request.url())
                .build());
        }
        roomQueryUseCase.saveRoomImages(images);
    }

    private List<RoomImage> getRoomImages(List<RoomImageDeleteRequest> requests) {
        List<RoomImage> images = new ArrayList<>();
        for (RoomImageDeleteRequest request : requests) {
            images.add(roomQueryUseCase.findRoomImage(request.id()));
        }
        return images;
    }

    private void validateRoomStatus(String status) {
        boolean flag = false;
        for (RoomStatus roomStatus : RoomStatus.values()) {
            if (roomStatus.name().equals(status)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new InvalidRoomStatusException();
        }
    }

    private void createRoomStock(Room room) {
        for (int i = 0; i < 30; i++) {
            roomQueryUseCase.saveRoomStock(RoomStock.builder()
                .room(room)
                .count(room.getAmount())
                .date(LocalDate.now().plusDays(i))
                .build());
        }
    }
}
