package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.exception.DuplicateRoomNameException;
import com.backoffice.upjuyanolja.domain.room.exception.InvalidTimeFormatException;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        roomNameValidate(request.name());

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
            .checkInTime(timeFormatter(request.checkInTime()))
            .checkOutTime(timeFormatter(request.checkOutTime()))
            .amount(request.amount())
            .images(new ArrayList<>())
            .option(RoomOptionRequest.toEntity(request.option()))
            .build()
        );
        roomQueryUseCase.saveRoomImages(RoomImageRequest.toEntity(room, request.images()));

        return RoomInfoResponse.of(roomQueryUseCase.getRoomById(room.getId()));
    }

    private void roomNameValidate(String name) {
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

    private LocalTime timeFormatter(String dateTimeString) {
        try {
            String pattern = "HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            return LocalTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeFormatException();
        }
    }
}
