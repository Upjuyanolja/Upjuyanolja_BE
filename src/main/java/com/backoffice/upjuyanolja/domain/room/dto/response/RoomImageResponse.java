package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

/**
 * 객실 이미지 응답 DTO Record
 *
 * @param id  객실 이미지 식별자
 * @param url 객실 이미지 URL
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomImageResponse(long id, String url) {

    /**
     * 객실 이미지 Entity 리스트로 객실 이미지 응답 DTO 리스트를 생성하는 정적 팩토리 메서드
     *
     * @param roomImages 객실 이미지 Entity 리스트
     * @return 객실 이미지 응답 DTO 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static List<RoomImageResponse> of(
        List<RoomImage> roomImages
    ) {
        List<RoomImageResponse> roomImageResponses = new ArrayList<>();
        for (RoomImage roomImage : roomImages) {
            roomImageResponses.add(RoomImageResponse.builder()
                .id(roomImage.getId())
                .url(roomImage.getUrl())
                .build());
        }
        return roomImageResponses;
    }
}
