package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import lombok.Builder;

public interface RoomCommandUseCase {

    RoomInfoResponse registerRoom(long memberId, long accommodationId, RoomRegisterRequest request);

    RoomInfoResponse saveRoom(Accommodation accommodation, RoomRegisterRequest request);

    RoomInfoResponse modifyRoom(long memberId, long roomId, RoomUpdateRequest request);

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

        public static RoomUpdate of(RoomUpdateRequest request) {
            return RoomUpdate.builder()
                .name(request.name())
                .status(request.status())
                .price(request.price())
                .defaultCapacity(request.defaultCapacity())
                .maxCapacity(request.maxCapacity())
                .checkInTime(request.checkInTime())
                .checkOutTime(request.checkOutTime())
                .amount(request.amount())
                .option(RoomOptionUpdate.of(request.option()))
                .build();
        }
    }

    @Builder
    record RoomOptionUpdate(
        boolean airCondition,
        boolean tv,
        boolean internet
    ) {

        public static RoomOptionUpdate of(RoomOptionRequest request) {
            return RoomOptionUpdate.builder()
                .airCondition(request.airCondition())
                .tv(request.tv())
                .internet(request.internet())
                .build();
        }
    }
}
