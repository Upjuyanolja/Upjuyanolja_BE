package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import lombok.Builder;

public interface RoomCommandUseCase {

    RoomInfoResponse registerRoom(long memberId, long accommodationId, RoomRegisterRequest request);

    RoomInfoResponse modifyRoom(long memberId, long roomId, RoomUpdateRequest request);

    RoomInfoResponse deleteRoom(long memberId, long roomId);

    @Builder
    record RoomUpdate(
        String name,
        String status,
        int price,
        int defaultCapacity,
        int maxCapacity,
        String checkInTime,
        String checkOutTime,
        int amount,
        RoomOptionUpdate option
    ) {

    }

    @Builder
    record RoomOptionUpdate(
        boolean airCondition,
        boolean tv,
        boolean internet
    ) {

    }
}
