package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomInfoResponse(
    long id,
    String name,
    String status,
    int defaultCapacity,
    int maxCapacity,
    String checkInTime,
    String checkOutTime,
    int price,
    int amount,
    List<RoomImageResponse> images,
    RoomOptionResponse option
) {

    public static RoomInfoResponse of(Room room) {
        return RoomInfoResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .defaultCapacity(room.getStandard())
            .maxCapacity(room.getCapacity())
            .checkInTime(room.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOut().format(DateTimeFormatter.ofPattern("HH:mm")))
            .price(room.getPrice().getOffWeekDaysMinFee())
            .amount(room.getAmount())
            .images(RoomImageResponse.of(room.getImages()))
            .option(RoomOptionResponse.of(room.getOption()))
            .build();
    }
}
