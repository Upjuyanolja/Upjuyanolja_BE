package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationImageResponse(long id, String url) {

    public static List<AccommodationImageResponse> of(
        List<AccommodationImage> accommodationImages
    ) {
        List<AccommodationImageResponse> images = new ArrayList<>();
        accommodationImages.forEach(image -> images.add(AccommodationImageResponse.builder()
            .id(image.getId())
            .url(image.getUrl())
            .build()));
        return images;
    }
}
