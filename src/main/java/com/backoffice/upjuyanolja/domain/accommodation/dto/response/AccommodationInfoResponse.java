package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import java.util.List;
import lombok.Builder;

/**
 * 숙소 정보 응답 DTO Record
 *
 * @param accommodationId 숙소 식별자
 * @param name            숙소 이름
 * @param category        숙소 카테고리
 * @param description     숙소 설명
 * @param address         숙소 주소
 * @param option          숙소 옵션 응답 DTO
 * @param images          숙소 이미지 응답 DTO 리스트
 * @param rooms           숙소 객실 정보 응답 DTO 리스트
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record AccommodationInfoResponse(
    long accommodationId,
    String name,
    String category,
    String description,
    String address,
    AccommodationOptionResponse option,
    List<AccommodationImageResponse> images,
    List<RoomInfoResponse> rooms
) {

    /**
     * 숙소 Entity, 숙소 옵션 Entity, 숙소 이미지 Entity 리스트, 객실 정보 응답 DTO로 숙소 정보 응답 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param accommodation       숙소 Entity
     * @param accommodationOption 숙소 옵션 Entity
     * @param accommodationImages 숙소 이미지 Entity 리스트
     * @param roomInfoResponses   객실 정보 응답 DTO
     * @return 숙소 정보 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static AccommodationInfoResponse of(
        Accommodation accommodation,
        AccommodationOption accommodationOption,
        List<AccommodationImage> accommodationImages,
        List<RoomInfoResponse> roomInfoResponses
    ) {
        return AccommodationInfoResponse.builder()
            .accommodationId(accommodation.getId())
            .name(accommodation.getName())
            .category(accommodation.getCategory().getName())
            .description(accommodation.getDescription())
            .address(accommodation.getAddress() + "\n" + accommodation.getDetailAddress())
            .option(AccommodationOptionResponse.of(accommodationOption))
            .images(AccommodationImageResponse.of(accommodationImages))
            .rooms(roomInfoResponses)
            .build();
    }
}
