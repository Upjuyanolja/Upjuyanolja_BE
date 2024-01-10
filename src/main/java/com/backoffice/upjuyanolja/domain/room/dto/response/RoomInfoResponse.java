package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomInfoResponse(
    long id,
    String name,
    int defaultCapacity,
    int maxCapacity,
    String checkInTime,
    String checkOutTime,
    int price,
    int amount,
    String status,
    List<RoomImageResponse> images,
    RoomOptionResponse option
) {

    public static RoomInfoResponse of(Room room) {
        return RoomInfoResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .defaultCapacity(room.getStandard())
            .maxCapacity(room.getCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .price(room.getRoomPrice().getOffWeekDaysMinFee())
            .amount(room.getAmount())
            .status(room.getRoomStatus().name())
            .images(RoomImageResponse.of(room.getRoomImages()))
            .option(RoomOptionResponse.of(room.getRoomOption()))
            .build();
    }
}
