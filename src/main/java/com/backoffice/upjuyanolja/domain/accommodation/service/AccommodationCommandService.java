package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationOptionRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationImageNotExistsException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOptionRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationCommandUseCase;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.service.RoomCommandService;
import com.backoffice.upjuyanolja.domain.room.service.RoomQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 숙소 생성, 수정, 삭제 Service Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class AccommodationCommandService implements AccommodationCommandUseCase {

    /**
     * 숙소 Repository Interface
     */
    private final AccommodationRepository accommodationRepository;

    /**
     * 숙소 옵션 Repository Interface
     */
    private final AccommodationOptionRepository accommodationOptionRepository;

    /**
     * 숙소 카테고리 Repository Interface
     */
    private final CategoryRepository categoryRepository;

    /**
     * 숙소 소유권 Repository Interface
     */
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;

    /**
     * 숙소 이미지 Repository Interface
     */
    private final AccommodationImageRepository accommodationImageRepository;

    /**
     * 회원 조회 Service Class
     */
    private final MemberGetService memberGetService;

    /**
     * 객실 생성, 수정, 삭제 Service Class
     */
    private final RoomCommandService roomCommandService;

    /**
     * 객실 조회 Service Class
     */
    private final RoomQueryService roomQueryService;

    /**
     * 숙소 생성/저장 메서드
     *
     * @param memberId                     숙소를 생성하는 업주 회원 식별자
     * @param accommodationRegisterRequest 숙소 등록 요청 DTO
     * @return 저장한 숙소 정보 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    @Transactional
    public AccommodationInfoResponse createAccommodation(
        long memberId,
        AccommodationRegisterRequest accommodationRegisterRequest
    ) {
        // 1. 요청 검증
        accommodationImageRequestsValidate(accommodationRegisterRequest.images());
        roomRegisterRequestsValidate(accommodationRegisterRequest.rooms());

        // 2. 회원, 카테고리 조회
        Member member = memberGetService.getMemberById(memberId);
        Category category = getCategory(accommodationRegisterRequest.category());

        // 3. 숙소, 숙소 옵션, 숙소 이미지, 숙소 소유권 저장
        Accommodation accommodation = accommodationRepository.save(
            AccommodationRegisterRequest.toEntity(accommodationRegisterRequest, category)
        );
        List<AccommodationImage> images = accommodationImageRepository.saveAll(
            AccommodationImageRequest.toEntities(accommodation,
                accommodationRegisterRequest.images())
        );
        AccommodationOption option = accommodationOptionRepository.save(
            AccommodationOptionRequest.toEntity(accommodation,
                accommodationRegisterRequest.option())
        );
        accommodationOwnershipRepository.save(AccommodationOwnership.builder()
            .accommodation(accommodation)
            .member(member)
            .build());

        // 4. 객실 저장
        accommodationRegisterRequest.rooms().forEach(
            roomRegisterRequest -> roomCommandService.saveRoom(accommodation, roomRegisterRequest)
        );
        List<RoomInfoResponse> room = roomQueryService.getRoomsInfo(accommodation);

        return AccommodationInfoResponse.of(accommodation, option, images, room);
    }

    /**
     * 숙소 이미지 등록 요청 검증 메서드
     *
     * @param accommodationImageRequests 숙소 이미지 저장 요청 DTO
     * @throws AccommodationImageNotExistsException 숙소 이미지를 하나 이상 포함지 않는 경우 예외 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void accommodationImageRequestsValidate(
        List<AccommodationImageRequest> accommodationImageRequests) {
        if (accommodationImageRequests.isEmpty()) {
            throw new AccommodationImageNotExistsException();
        }
    }

    /**
     * 객실 등록 요청 검증 메서드
     *
     * @param roomRegisterRequests 객실 등록 요청 DTO
     * @throws RoomNotFoundException 객실을 하나 이상 포함하지 않는 경우 예외처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private void roomRegisterRequestsValidate(List<RoomRegisterRequest> roomRegisterRequests) {
        if (roomRegisterRequests.isEmpty()) {
            throw new RoomNotFoundException();
        }
    }

    /**
     * 문자열과 일치하는 하위 카테고리 조회 메서드
     *
     * @param category 조회하고자 하는 카테고리 이름
     * @return 카테고리 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private Category getCategory(String category) {
        return categoryRepository.findCategoryByName(category)
            .orElseThrow(WrongCategoryException::new);
    }
}
