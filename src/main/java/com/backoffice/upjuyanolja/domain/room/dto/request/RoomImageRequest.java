package com.backoffice.upjuyanolja.domain.room.dto.request;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

/**
 * 객실 이미지 등록 요청 DTO Record
 *
 * @param url 등록할 객실 이미지 URL
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomImageRequest(
    @NotBlank(message = "객실 이미지 URL을 입력하세요.")
    String url
) {

    /**
     * 객실 이미지 등록 요청 DTO를 객실 이미지 Entity로 변환하는 메서드
     *
     * @param room              이미지를 등록할 객실 Entity
     * @param roomImageRequests 객실 이미지 등록 요청 DTO 리스트
     * @return 객실 이미지 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static List<RoomImage> toEntities(Room room, List<RoomImageRequest> roomImageRequests) {
        List<RoomImage> images = new ArrayList<>();
        roomImageRequests.forEach(image -> images.add(RoomImage.builder()
            .room(room)
            .url(image.url())
            .build()));
        return images;
    }
}
