package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationQueryService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageAddRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageDeleteRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
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
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomOptionRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomPriceRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 객실 생성, 수정, 삭제 Service Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class RoomCommandService implements RoomCommandUseCase {

    /**
     * 객실 Repository Interface
     */
    private final RoomRepository roomRepository;

    /**
     * 객실 이미지 Repository Interface
     */
    private final RoomImageRepository roomImageRepository;

    /**
     * 객실 옵션 Repository Interface
     */
    private final RoomOptionRepository roomOptionRepository;

    /**
     * 객실 가격 Repository Interface
     */
    private final RoomPriceRepository roomPriceRepository;

    /**
     * 객실 재고 Repository Interface
     */
    private final RoomStockRepository roomStockRepository;

    /**
     * 회원 조회 Service Class
     */
    private final MemberGetService memberGetService;

    /**
     * 숙소 조회 Service Class
     */
    private final AccommodationQueryService accommodationQueryService;

    /**
     * 객실 조회 Service Class
     */
    private final RoomQueryService roomQueryService;

    /**
     * Entity Manager Interface
     */
    private final EntityManager em;

    /**
     * 객실 등록 메서드
     *
     * @param memberId            객실을 등록하는 업주 회원 식별자
     * @param accommodationId     객실을 등록할 숙소 식별자
     * @param roomRegisterRequest 객실 등록 요청 DTO
     * @return 등록한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    @Transactional
    public RoomInfoResponse registerRoom(
        long memberId,
        long accommodationId,
        RoomRegisterRequest roomRegisterRequest
    ) {
        // 1. 회원, 숙소 조회
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = accommodationQueryService.getAccommodationById(
            accommodationId
        );

        // 2. 소유권 확인
        roomQueryService.checkOwnership(member, accommodation);

        // 3. 객실 저장
        return saveRoom(accommodation, roomRegisterRequest);
    }

    /**
     * 객실 정보 수정 메서드
     *
     * @param memberId          객실 정보를 수정하는 업주 회원 식별자
     * @param roomId            정보를 수정할 객실 식별자
     * @param roomUpdateRequest 객실 정보 수정 요청 DTO
     * @return 수정한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    @Transactional
    public RoomInfoResponse modifyRoom(
        long memberId,
        long roomId,
        RoomUpdateRequest roomUpdateRequest
    ) {
        // 1. 회원, 객실 조회
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomQueryService.findRoomById(roomId);

        // 2. 요청 검증
        if (!room.getName().equals(roomUpdateRequest.name())) {
            validateRoomName(roomUpdateRequest.name(), room.getAccommodation());
        }
        validateRoomStatus(roomUpdateRequest.status());

        // 3. 소유권 확인
        roomQueryService.checkOwnership(member, room.getAccommodation());

        // 4. 객실, 객실 옵션, 객실 가격, 객실 이미지 수정
        updateRoom(room, roomUpdateRequest);
        RoomOption roomOption = updateRoomOption(room, roomUpdateRequest.option().toUpdateDto());
        RoomPrice roomPrice = updateRoomPrice(room, roomUpdateRequest.price());
        addRoomImages(room, roomUpdateRequest.addImages());
        deleteRoomImages(roomUpdateRequest.deleteImages());

        // 5. 객실 관련 엔터티 Refresh
        List<RoomImage> roomImages = roomQueryService.findRoomImageByRoom(room);

        return RoomInfoResponse.of(room, roomOption, roomImages, roomPrice.getOffWeekDaysMinFee());
    }

    /**
     * 객실 삭제 메서드
     *
     * @param memberId 객실을 삭제하는 업주 회원 식별자
     * @param roomId   삭제할 객실 식별자
     * @return 삭제한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    @Transactional
    public RoomInfoResponse deleteRoom(long memberId, long roomId) {
        // 1. 회원, 객실 조회
        Member member = memberGetService.getMemberById(memberId);
        Room room = roomQueryService.findRoomById(roomId);

        // 2. 소유권 확인
        roomQueryService.checkOwnership(member, room.getAccommodation());

        // 3. 요청 검증
        if (isLastRoom(room)) {
            throw new CanNotDeleteLastRoomException();
        }

        // 4. 객실 논리 삭제
        room.delete(LocalDateTime.now());

        // 5. 객실 옵션, 객실 가격, 객실 이미지 조회
        RoomOption roomOption = roomQueryService.findRoomOptionByRoom(room);
        RoomPrice roomPrice = roomQueryService.findRoomPriceByRoom(room);
        List<RoomImage> roomImages = roomQueryService.findRoomImageByRoom(room);

        return RoomInfoResponse.of(room, roomOption, roomImages, roomPrice.getOffWeekDaysMinFee());
    }

    /**
     * 객실 이름 검증 메서드
     * <p>
     * 같은 숙소에 속한 객실들 중 이름이 중복되는지 확인합니다.
     *
     * @param roomName      검증하고자 하는 객실 이름
     * @param accommodation 객실이 속한 숙소 Entity
     * @throws DuplicateRoomNameException 특정 숙소 내 객실 이름이 중복되는 경우 에러 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void validateRoomName(String roomName, Accommodation accommodation) {
        if (roomRepository.existsRoomByNameAndAccommodation(roomName, accommodation)) {
            throw new DuplicateRoomNameException();
        }
    }

    /**
     * 객실 상태 검증 메서드
     * <p>
     * 문자열을 RoomStatus Enum에서 찾을 수 있는 유효한 객실 상태인지 확인합니다.
     *
     * @param stringRoomStatus 객실 상태 문자열
     * @throws InvalidRoomStatusException 문자열을 RoomStatus Enum에서 찾을 수 없는 경우 에러 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void validateRoomStatus(String stringRoomStatus) {
        boolean flag = false;
        for (RoomStatus roomStatus : RoomStatus.values()) {
            if (roomStatus.name().equals(stringRoomStatus)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new InvalidRoomStatusException();
        }
    }

    /**
     * 객실 저장 메서드
     *
     * @param accommodation       객실을 등록하고자하는 숙소 Entity
     * @param roomRegisterRequest 객실 등록 요청 DTO
     * @return 저장한 객실 정보
     * @throws RoomImageNotExistsException 객실 이미지가 하나 이상 존재하지 않는 경우 에러 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public RoomInfoResponse saveRoom(
        Accommodation accommodation,
        RoomRegisterRequest roomRegisterRequest
    ) {
        // 1. 요청 검증
        validateRoomName(roomRegisterRequest.name(), accommodation);
        if (roomRegisterRequest.images().isEmpty()) {
            throw new RoomImageNotExistsException();
        }

        // 2. 객실, 객실 가격, 객실 옵션, 객실 이미지, 객실 재고 저장
        Room room = roomRepository.save(roomRegisterRequest.toEntity(accommodation));
        RoomPrice roomPrice = roomPriceRepository.save(roomRegisterRequest.toRoomPriceEntity(room));
        RoomOption roomOption = roomOptionRepository.save(
            roomRegisterRequest.option().toEntity(room)
        );
        List<RoomImage> roomImages = roomImageRepository.saveAll(
            RoomImageRequest.toEntities(room, roomRegisterRequest.images())
        );
        saveRoomStocks(room);

        return RoomInfoResponse.of(room, roomOption, roomImages, roomPrice.getOffWeekDaysMinFee());
    }

    /**
     * 객실 재고 저장 메서드
     * <p>
     * 오늘을 포함한 30일치의 재고를 저장합니다.
     *
     * @param room 재고를 저장하려는 객실 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void saveRoomStocks(Room room) {
        for (int i = 0; i < 30; i++) {
            roomStockRepository.save(RoomStock.builder()
                .room(room)
                .count(room.getAmount())
                .date(LocalDate.now().plusDays(i))
                .build());
        }
    }

    /**
     * 객실 정보 수정 메서드
     *
     * @param room              수정할 객실 Entity
     * @param roomUpdateRequest 객실 수정 요청 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void updateRoom(Room room, RoomUpdateRequest roomUpdateRequest) {
        if (room.getAmount() != roomUpdateRequest.amount()) {
            updateRoomStock(room, roomUpdateRequest.amount() - room.getAmount());
        }
        room.update(roomUpdateRequest.toRoomUpdateDto());
    }

    /**
     * 객실 가격 수정 메서드
     *
     * @param room  가격을 수정하려는 객실 Entity
     * @param price 수정 객실 가격
     * @return 수정된 객실 가격 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private RoomPrice updateRoomPrice(Room room, int price) {
        RoomPrice roomPrice = roomQueryService.findRoomPriceByRoom(room);
        roomPrice.updateRoomPrice(price);

        return roomPrice;
    }

    /**
     * 객실 옵션 수정 메서드
     *
     * @param room                옵션을 수정하려는 객실 Entity
     * @param roomOptionUpdateDto 수정할 옵션 DTO
     * @return 수정한 객실 옵션 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private RoomOption updateRoomOption(Room room, RoomOptionUpdateDto roomOptionUpdateDto) {
        RoomOption roomOption = roomQueryService.findRoomOptionByRoom(room);
        roomOption.update(roomOptionUpdateDto);

        return roomOption;
    }

    /**
     * 객실 이미지 추가 저장 메서드
     *
     * @param room                 이미지를 저장할 객실
     * @param roomImageAddRequests 객실 이미지 저장 요청 DTO 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void addRoomImages(Room room, List<RoomImageAddRequest> roomImageAddRequests) {
        for (RoomImageAddRequest roomImageAddRequest : roomImageAddRequests) {
            roomImageRepository.save(RoomImage.builder()
                .room(room)
                .url(roomImageAddRequest.url())
                .build());
        }
    }

    /**
     * 객실 이미지 삭제 메서드
     *
     * @param roomImageDeleteRequests 객실 이미지 삭제 요청 DTO 리스트
     * @throws RoomImageNotFoundException 객실 이미지를 찾을 수 없는 경우 에러 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void deleteRoomImages(List<RoomImageDeleteRequest> roomImageDeleteRequests) {
        for (RoomImageDeleteRequest roomImageDeleteRequest : roomImageDeleteRequests) {
            roomImageRepository.delete(roomImageRepository.findById(roomImageDeleteRequest.id())
                .orElseThrow(RoomImageNotFoundException::new));
        }
    }

    /**
     * 객실 재고 수정 메서드
     *
     * @param room     재고를 수정하려는 객실 Entity
     * @param quantity 수정할 재고
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void updateRoomStock(Room room, int quantity) {
        List<RoomStock> stocks = roomStockRepository.findAllByRoomAndAfterToday(room);
        stocks.forEach(stock -> stock.update(quantity));
    }

    /**
     * 숙소의 마지막 객실 여부 체크 메서드
     *
     * @param room 마지막 객실인지 확인할 객실
     * @return 마지막 객실 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private boolean isLastRoom(Room room) {
        return roomRepository.countByAccommodationId(room.getAccommodation().getId()) == 1;
    }
}
