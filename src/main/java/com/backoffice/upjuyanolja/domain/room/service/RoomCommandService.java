package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageAddRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageDeleteRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.exception.DuplicateRoomNameException;
import com.backoffice.upjuyanolja.domain.room.exception.InvalidRoomStatusException;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import com.backoffice.upjuyanolja.global.util.DateTimeParser;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomCommandService implements RoomCommandUseCase {

    private final MemberGetService memberGetService;
    private final RoomQueryUseCase roomQueryUseCase;
    private final AccommodationQueryUseCase accommodationQueryUseCase;

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
        validateRoomName(request.name());

        Room room = roomQueryUseCase.saveRoom(accommodation, Room.builder()
            .name(request.name())
            .price(RoomPrice.builder()
                .offWeekDaysMinFee(request.price())
                .offWeekendMinFee(request.price())
                .peakWeekDaysMinFee(request.price())
                .peakWeekendMinFee(request.price())
                .build())
            .defaultCapacity(request.defaultCapacity())
            .defaultCapacity(request.maxCapacity())
            .checkInTime(DateTimeParser.timeParser(request.checkInTime()))
            .checkOutTime(DateTimeParser.timeParser(request.checkOutTime()))
            .amount(request.amount())
            .images(new ArrayList<>())
            .option(RoomOptionRequest.toEntity(request.option()))
            .build()
        );
        roomQueryUseCase.saveRoomImages(RoomImageRequest.toEntity(room, request.images()));

        return RoomInfoResponse.of(roomQueryUseCase.findRoomById(room.getId()));
    }

    @Override
    public RoomInfoResponse modifyRoom(long memberId, long roomId, RoomUpdateRequest request) {
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomQueryUseCase.findRoomById(roomId);

        validateRoomStatus(request.status());
        checkOwnership(member, room.getAccommodation());
        updateRoom(room, request);

        return RoomInfoResponse.of(roomQueryUseCase.findRoomById(roomId));
    }

    private void validateRoomName(String name) {
        if (roomQueryUseCase.existsRoomByName(name)) {
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
        room.updateRoom(request.toRoomUpdateDto());
        addRoomImages(room, request.addImages());
        roomQueryUseCase.deleteRoomImages(getRoomImages(request.deleteImages()));
    }

    private void addRoomImages(Room room, List<RoomImageAddRequest> requests) {
        List<RoomImage> images = new ArrayList<>();
        requests.forEach(roomImages -> images.add(RoomImage.builder()
            .room(room)
            .url(roomImages.url())
            .build()));
        roomQueryUseCase.saveRoomImages(images);
    }

    private List<RoomImage> getRoomImages(List<RoomImageDeleteRequest> requests) {
        List<RoomImage> images = new ArrayList<>();
        requests.forEach(request -> images.add(roomQueryUseCase.findRoomImage(request.id())));

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
}
