package com.backoffice.upjuyanolja.domain.room.dto.request;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase.RoomOptionUpdateDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 객실 옵션 등록 요청 DTO Record
 *
 * @param airCondition 객실 에어컨 여부
 * @param tv           객실 TV 여부
 * @param internet     객실 인터넷 여부
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomOptionRequest(
    @NotNull(message = "에어컨 여부를 입력하세요.")
    boolean airCondition,

    @NotNull(message = "TV 여부를 입력하세요.")
    boolean tv,

    @NotNull(message = "인터넷 여부를 입력하세요.")
    boolean internet
) {

    /**
     * 객실 옵션 등록 요청 DTO를 객실 옵션 Entity로 변환하는 메서드
     *
     * @param room 객실 Entity
     * @return 객실 옵션 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public RoomOption toEntity(Room room) {
        return RoomOption.builder()
            .room(room)
            .airCondition(this.airCondition)
            .tv(this.tv)
            .internet(this.internet)
            .build();
    }

    /**
     * 객실 옵션 등록 요청 DTO를 객실 옵션 수정 DTO로 변환하는 메서드
     *
     * @return 객실 옵션 수정 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public RoomOptionUpdateDto toUpdateDto() {
        return RoomOptionUpdateDto.builder()
            .airCondition(this.airCondition)
            .tv(this.tv)
            .internet(this.internet)
            .build();
    }
}
