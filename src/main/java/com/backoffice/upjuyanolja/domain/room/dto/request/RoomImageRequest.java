package com.backoffice.upjuyanolja.domain.room.dto.request;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomImageRequest(
    @NotBlank(message = "객실 이미지 URL을 입력하세요.")
    String url
) {

    public static List<RoomImage> toEntity(Room room, List<RoomImageRequest> request) {
        List<RoomImage> images = new ArrayList<>();
        request.forEach(image -> images.add(RoomImage.builder()
            .room(room)
            .url(image.url())
            .build()));
        return images;
    }
}
