package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
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

    public static RoomInfoResponse of(Room room, RoomOption option) {
        return RoomInfoResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .price(room.getPrice().getOffWeekDaysMinFee())
            .amount(room.getAmount())
            .status(room.getStatus().name())
            .images(RoomImageResponse.of(room.getImages()))
            .option(RoomOptionResponse.of(option))
            .build();
    }
}
