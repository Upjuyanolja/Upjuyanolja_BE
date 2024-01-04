package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomResponse(
    Long id,
    String name,
    int basePrice,
    int discountPrice,
    int defaultCapacity,
    int maxCapacity,
    String checkInTime,
    String checkOutTime,
    boolean soldOut,
    int count,
    List<CouponRoomResponse> coupons,
    List<String> images,
    RoomOptionResponse roomOption
) {

    public static RoomResponse from(
        Room room, int discountPrice, boolean soldOut,
        List<CouponRoomResponse> couponRoomResponses

    ) {
        return RoomResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .basePrice(room.getRoomPrice().getOffWeekDaysMinFee())
            .discountPrice(discountPrice)
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .soldOut(soldOut)
            .count(room.getCount())
            .coupons(
                couponRoomResponses
            )
            .images(
                room.getRoomImages().stream()
                    .map(image -> image.getUrl())
                    .toList()
            )
            .roomOption(RoomOptionResponse.from(room.getRoomOption()))
            .build();
    }

}
