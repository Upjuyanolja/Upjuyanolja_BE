package com.backoffice.upjuyanolja.domain.accommodation.dto.request;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

/**
 * 숙소 등록 요청 DTO Record
 *
 * @param name          등록할 숙소 이름
 * @param category      등록할 숙소 카테고리
 * @param description   등록할 숙소 설명
 * @param thumbnail     등록할 숙소 대표 이미지 URL
 * @param address       등록할 숙소 주소
 * @param detailAddress 등록할 숙소 상세 주소
 * @param zipCode       등록할 숙소 우편 번호
 * @param option        등록할 숙소 옵션 등록 요청 DTO
 * @param images        등록할 숙소 이미지 등록 요청 DTO 리스트
 * @param rooms         등록할 숙소 객실 등록 요청 DTO
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record AccommodationRegisterRequest(
    @NotBlank(message = "숙소 이름을 입력하세요.")
    @Size(min = 1, max = 30, message = "숙소 이름을 1~30자로 입력하세요.")
    String name,

    @NotBlank(message = "숙소 유형을 입력하세요.")
    String category,

    @NotBlank(message = "숙소 설명을 입력하세요.")
    String description,

    @NotBlank(message = "숙소 대표 이미지를 입력하세요.")
    String thumbnail,

    @NotBlank(message = "숙소 주소를 입력하세요.")
    String address,

    String detailAddress,

    @Pattern(regexp = "^\\d{5}$", message = "우편번호로 국가기초구역번호(5자리)를 입력하세요.")
    String zipCode,

    @NotNull(message = "숙소 옵션을 입력하세요.")
    AccommodationOptionRequest option,

    @NotNull(message = "숙소 이미지를 한 개 이상 입력하세요.")
    List<AccommodationImageRequest> images,

    @NotNull(message = "객실을 하나 이상 입력하세요.")
    List<RoomRegisterRequest> rooms
) {

    /**
     * 숙소 등록 요청 DTO를 숙소 Entity로 변환하는 메서드
     *
     * @param accommodationRegisterRequest 숙소 등록 요청 DTO
     * @param category                     숙소 카테고리 Entity
     * @return 숙소 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static Accommodation toEntity(
        AccommodationRegisterRequest accommodationRegisterRequest,
        Category category
    ) {
        return Accommodation.builder()
            .name(accommodationRegisterRequest.name())
            .category(category)
            .description(accommodationRegisterRequest.description())
            .thumbnail(accommodationRegisterRequest.thumbnail())
            .address(accommodationRegisterRequest.address())
            .detailAddress(accommodationRegisterRequest.detailAddress())
            .zipCode(accommodationRegisterRequest.zipCode())
            .build();
    }
}
