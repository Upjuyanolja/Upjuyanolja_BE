package com.backoffice.upjuyanolja.domain.room.dto.request;

import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RoomOptionRequest(
    @NotNull(message = "에어컨 여부를 입력하세요.")
    boolean airCondition,

    @NotNull(message = "TV 여부를 입력하세요.")
    boolean tv,

    @NotNull(message = "인터넷 여부를 입력하세요.")
    boolean internet
) {

    public static RoomOption toEntity(RoomOptionRequest request) {
        return RoomOption.builder()
            .airCondition(request.airCondition)
            .tv(request.tv)
            .internet(request.internet)
            .build();
    }
}
