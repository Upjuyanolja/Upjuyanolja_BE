package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomImageResponse(long id, String url) {

    public static List<RoomImageResponse> of(
        List<RoomImage> roomImages
    ) {
        List<RoomImageResponse> images = new ArrayList<>();
        roomImages.forEach(image -> images.add(RoomImageResponse.builder()
            .id(image.getId())
            .url(image.getUrl())
            .build()));
        return images;
    }
}
