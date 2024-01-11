package com.backoffice.upjuyanolja.domain.accommodation.dto.request;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationImageRequest(
    @NotBlank(message = "이미지 URL을 입력하세요.")
    String url
) {

    public static List<AccommodationImage> toEntity(Accommodation accommodation,
        List<AccommodationImageRequest> requests) {
        List<AccommodationImage> images = new ArrayList<>();
        requests.forEach(request -> images.add(AccommodationImage.builder()
            .accommodation(accommodation)
            .url(request.url)
            .build()));
        return images;
    }
}
