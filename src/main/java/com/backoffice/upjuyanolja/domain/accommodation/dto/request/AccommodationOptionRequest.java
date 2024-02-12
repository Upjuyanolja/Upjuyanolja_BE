package com.backoffice.upjuyanolja.domain.accommodation.dto.request;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 숙소 옵션 등록 요청 DTO Record
 *
 * @param cooking  숙소 객실 내 취사 여부
 * @param parking  숙소 주차 시설 여부
 * @param pickup   숙소 픽업 서비스 여부
 * @param barbecue 숙소 바베큐장 여부
 * @param fitness  숙소 피트니스 센터 여부
 * @param karaoke  숙소 노래방 여부
 * @param sauna    숙소 사우나실 여부
 * @param sports   숙소 스포츠 시설 여부
 * @param seminar  숙소 세미나실 여부
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record AccommodationOptionRequest(
    @NotNull(message = "객실 내 취사 여부를 입력하세요.")
    boolean cooking,

    @NotNull(message = "주차 시설 여부를 입력하세요.")
    boolean parking,

    @NotNull(message = "픽업 서비스 여부를 입력하세요.")
    boolean pickup,

    @NotNull(message = "바베큐장 여부를 입력하세요.")
    boolean barbecue,

    @NotNull(message = "피트니스 센터 여부를 입력하세요.")
    boolean fitness,

    @NotNull(message = "노래방 여부를 입력하세요.")
    boolean karaoke,

    @NotNull(message = "사우나실 여부를 입력하세요.")
    boolean sauna,

    @NotNull(message = "스포츠 시설 여부를 입력하세요.")
    boolean sports,

    @NotNull(message = "세미나실 여부를 입력하세요.")
    boolean seminar
) {

    /**
     * 숙소 옵션 등록 요청 DTO를 숙소 옵션 Entity로 변환하는 메서드
     *
     * @param accommodation              옵션을 등록할 숙소 Entity
     * @param accommodationOptionRequest 숙소 옵션 등록 요청 DTO
     * @return 숙소 옵션 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static AccommodationOption toEntity(
        Accommodation accommodation,
        AccommodationOptionRequest accommodationOptionRequest
    ) {
        return AccommodationOption.builder()
            .accommodation(accommodation)
            .cooking(accommodationOptionRequest.cooking)
            .parking(accommodationOptionRequest.parking)
            .pickup(accommodationOptionRequest.pickup)
            .barbecue(accommodationOptionRequest.barbecue)
            .fitness(accommodationOptionRequest.fitness)
            .sauna(accommodationOptionRequest.sauna)
            .sports(accommodationOptionRequest.sports)
            .seminar(accommodationOptionRequest.seminar)
            .build();
    }
}
