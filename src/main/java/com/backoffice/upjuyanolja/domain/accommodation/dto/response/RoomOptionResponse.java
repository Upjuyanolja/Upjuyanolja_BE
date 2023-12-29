package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.RoomOption;
import lombok.Builder;

@Builder
public record RoomOptionResponse(
    boolean airCondition,
    boolean tv,
    boolean internet
) {

    public static RoomOptionResponse from(RoomOption roomOption) {
        return RoomOptionResponse.builder()
            .airCondition(roomOption.isAirCondition())
            .tv(roomOption.isTv())
            .internet(roomOption.isInternet())
            .build();
    }
}
