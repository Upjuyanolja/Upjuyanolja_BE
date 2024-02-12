package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import lombok.Builder;

/**
 * 객실 옵션 응답 DTO Record
 *
 * @param airCondition 객실 에어컨 여부
 * @param tv           객실 TV 여부
 * @param internet     객실 인터넷 여부
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomOptionResponse(
    boolean airCondition,
    boolean tv,
    boolean internet
) {

    /**
     * 객실 옵션 Entity로 객실 옵션 응답 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param roomOption 객실 옵션 Entity
     * @return 객실 옵션 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static RoomOptionResponse of(RoomOption roomOption) {
        return RoomOptionResponse.builder()
            .airCondition(roomOption.isAirCondition())
            .tv(roomOption.isTv())
            .internet(roomOption.isInternet())
            .build();
    }
}
