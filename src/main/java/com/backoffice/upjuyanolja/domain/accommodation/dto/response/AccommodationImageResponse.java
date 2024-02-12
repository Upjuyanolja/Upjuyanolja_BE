package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

/**
 * 숙소 이미지 등록 응답 DTO Record
 *
 * @param id  등록한 숙소 이미지 식별자
 * @param url 등록한 숙소 이미지 URL
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record AccommodationImageResponse(long id, String url) {

    /**
     * 숙소 이미지 Entity 리스트로 숙소 이미지 응답 DTO 리스트 객체를 생성하는 정적 팩토리 메서드
     *
     * @param accommodationImages 숙소 이미지 Entity 리스트
     * @return 숙소 이미지 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static List<AccommodationImageResponse> of(
        List<AccommodationImage> accommodationImages
    ) {
        List<AccommodationImageResponse> accommodationImageResponses = new ArrayList<>();
        for (AccommodationImage accommodationImage : accommodationImages) {
            accommodationImageResponses.add(AccommodationImageResponse.builder()
                .id(accommodationImage.getId())
                .url(accommodationImage.getUrl())
                .build());
        }
        return accommodationImageResponses;
    }
}
