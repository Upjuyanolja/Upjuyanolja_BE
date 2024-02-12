package com.backoffice.upjuyanolja.domain.accommodation.dto.request;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

/**
 * 숙소 이미지 등록 요청 DTO Record
 *
 * @param url 등록할 숙소 이미지 URL
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record AccommodationImageRequest(
    @NotBlank(message = "이미지 URL을 입력하세요.")
    String url
) {

    /**
     * 숙소 이미지 등록 요청 DTO 리스트를 숙소 이미지 Entity 리스트로 변환하는 메서드
     *
     * @param accommodation              이미지를 등록할 숙소 Entity
     * @param accommodationImageRequests 숙소 이미지 등록 요청 DTO 리스트
     * @return 숙소 이미지 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static List<AccommodationImage> toEntities(
        Accommodation accommodation,
        List<AccommodationImageRequest> accommodationImageRequests
    ) {
        List<AccommodationImage> accommodationImages = new ArrayList<>();
        for (AccommodationImageRequest accommodationImageRequest : accommodationImageRequests) {
            accommodationImages.add(AccommodationImage.builder()
                .accommodation(accommodation)
                .url(accommodationImageRequest.url)
                .build());
        }
        return accommodationImages;
    }
}
