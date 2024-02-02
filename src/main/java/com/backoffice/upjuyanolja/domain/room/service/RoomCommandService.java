package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
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
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.exception.CanNotDeleteLastRoomException;
import com.backoffice.upjuyanolja.domain.room.exception.DuplicateRoomNameException;
import com.backoffice.upjuyanolja.domain.room.exception.InvalidRoomStatusException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomImageNotExistsException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomImageNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomOptionRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomPriceRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import com.backoffice.upjuyanolja.global.util.DateTimeParser;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomCommandService implements RoomCommandUseCase {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomOptionRepository roomOptionRepository;
    private final RoomPriceRepository roomPriceRepository;
    private final RoomStockRepository roomStockRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;

    private final MemberGetService memberGetService;
    private final RoomQueryUseCase roomQueryUseCase;

    private final EntityManager em;

    @Override
    public RoomInfoResponse registerRoom(
        long memberId,
        long accommodationId,
        RoomRegisterRequest request
    ) {
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
        checkOwnership(member, accommodation);

        return saveRoom(accommodation, request);
    }

    public RoomInfoResponse saveRoom(Accommodation accommodation, RoomRegisterRequest request) {
        validateRoomName(request.name(), accommodation);
        if (request.images().isEmpty()) {
            throw new RoomImageNotExistsException();
        }

        Room room = roomRepository.save(Room.builder()
            .accommodation(accommodation)
            .name(request.name())
            .status(RoomStatus.SELLING)
            .defaultCapacity(request.defaultCapacity())
            .maxCapacity(request.maxCapacity())
            .checkInTime(DateTimeParser.timeParser(request.checkInTime()))
            .checkOutTime(DateTimeParser.timeParser(request.checkOutTime()))
            .amount(request.amount())
            .images(new ArrayList<>())
            .build()
        );

        roomImageRepository.saveAll(RoomImageRequest.toEntity(room, request.images()))
            .forEach(roomImage -> room.getImages().add(roomImage));

        createRoomStock(room);

        RoomOption roomOption = saveRoomOption(room, request.option());
        RoomPrice roomPrice = saveRoomPrice(room, request.price());

        return RoomInfoResponse.of(room, roomOption, roomPrice.getOffWeekDaysMinFee());
    }

    public RoomOption saveRoomOption(Room room, RoomOptionRequest request) {
        RoomOption saveRoomOption = roomOptionRepository.save(RoomOption.builder()
            .room(room)
            .tv(request.tv())
            .airCondition(request.airCondition())
            .internet(request.internet())
            .build()
        );

        return saveRoomOption;
    }

    private RoomPrice saveRoomPrice(Room room, int price) {
        RoomPrice saveRoomPrice = roomPriceRepository.save(RoomPrice.builder()
            .room(room)
            .offWeekDaysMinFee(price)
            .offWeekendMinFee(price)
            .peakWeekDaysMinFee(price)
            .peakWeekendMinFee(price)
            .build()
        );

        return saveRoomPrice;
    }

    @Override
    public RoomInfoResponse modifyRoom(long memberId, long roomId, RoomUpdateRequest request) {
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

        if (!room.getName().equals(request.name())) {
            validateRoomName(request.name(), room.getAccommodation());
        }
        validateRoomStatus(request.status());
        checkOwnership(member, room.getAccommodation());

        updateRoom(room, request);
        RoomOption roomOption = updateRoomOption(room, request.option().toRoomOptionUpdateDto());
        RoomPrice roomPrice = updateRoomPrice(room, request.price());


        em.flush();
        em.refresh(room);
        em.refresh(roomOption);
        em.refresh(roomPrice);

        return RoomInfoResponse.of(room, roomOption, roomPrice.getOffWeekDaysMinFee());
    }

    @Override
    public RoomInfoResponse deleteRoom(long memberId, long roomId) {
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        checkOwnership(member, room.getAccommodation());
        if (isLastRoom(room)) {
            throw new CanNotDeleteLastRoomException();
        }
        room.delete(LocalDateTime.now());

        RoomOption roomOption = roomQueryUseCase.findRoomOptionByRoom(room);
        roomOption.delete(LocalDateTime.now());
        RoomPrice roomPrice = roomQueryUseCase.findRoomPriceByRoom(room);
        roomPrice.delete(LocalDateTime.now());

        return RoomInfoResponse.of(room, roomOption, roomPrice.getOffWeekDaysMinFee());
    }

    @Transactional(readOnly = true)
    public List<RoomInfoResponse> getRoomInfoResponses(Accommodation accommodation) {
        List<RoomInfoResponse> roomInfoResponses = new ArrayList<>();
        accommodation.getRooms()
            .forEach(room -> {
                RoomOption option = roomQueryUseCase.findRoomOptionByRoom(room);
                int roomPrice = roomQueryUseCase.findRoomPriceByRoom(room).getOffWeekDaysMinFee();
                roomInfoResponses.add(RoomInfoResponse.of(room, option, roomPrice));
                }
            );

        return roomInfoResponses;
    }

    private void validateRoomName(String name, Accommodation accommodation) {
        if (roomRepository.existsRoomByNameAndAccommodation(name, accommodation)) {
            throw new DuplicateRoomNameException();
        }
    }

    private void checkOwnership(Member member, Accommodation accommodation) {
        if (!accommodationOwnershipRepository
            .existsAccommodationOwnershipByMemberAndAccommodation(member, accommodation)) {
            throw new NotOwnerException();
        }
    }

    private void updateRoom(Room room, RoomUpdateRequest request) {
        if (room.getAmount() != request.amount()) {
            updateRoomStock(room, request.amount() - room.getAmount());
        }
        room.updateRoom(request.toRoomUpdateDto());
        addRoomImages(room, request.addImages());
        roomImageRepository.deleteAll(getRoomImages(request.deleteImages()));
    }

    private RoomOption updateRoomOption(Room room, RoomOptionUpdate option) {
        RoomOption roomOption = roomQueryUseCase.findRoomOptionByRoom(room);
        roomOption.updateRoomOption(option);

        return roomOption;
    }

    private RoomPrice updateRoomPrice(Room room, int price) {
        RoomPrice roomPrice = roomQueryUseCase.findRoomPriceByRoom(room);
        roomPrice.updateRoomPrice(price);
        roomPriceRepository.save(roomPrice);

        return roomPrice;
    }

    private void updateRoomStock(Room room, int quantity) {
        List<RoomStock> stocks = roomStockRepository
            .findAllByRoomAndDateAfter(room, LocalDate.now().minusDays(1));
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
        roomImageRepository.saveAll(images);
    }

    private List<RoomImage> getRoomImages(List<RoomImageDeleteRequest> requests) {
        List<RoomImage> images = new ArrayList<>();
        for (RoomImageDeleteRequest request : requests) {
            images.add(roomImageRepository.findById(request.id())
                .orElseThrow(RoomImageNotFoundException::new));
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
            roomStockRepository.save(RoomStock.builder()
                .room(room)
                .count(room.getAmount())
                .date(LocalDate.now().plusDays(i))
                .build());
        }
    }

    private boolean isLastRoom(Room room) {
        return room.getAccommodation().getRooms().size() == 1;
    }
}
