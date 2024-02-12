package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import lombok.Builder;

/**
 * 숙소 이름 응답 DTO Record
 *
 * @param id   숙소 식별자
 * @param name 숙소 이름
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record AccommodationNameResponse(
    long id,
    String name
) {

    /**
     * 숙소 Entity로 숙소 이름 응답 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param accommodation 숙소 Entity
     * @return 숙소 이름 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static AccommodationNameResponse of(Accommodation accommodation) {
        return AccommodationNameResponse.builder()
            .id(accommodation.getId())
            .name(accommodation.getName())
            .build();
    }
}
