package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Room;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Builder;

@Builder
public record RoomResponse(
    Long id,
    String name,
    Integer basePrice,
    Integer discountPrice,
    Integer defaultCapacity,
    Integer maxCapacity,
    String checkInTime,
    String checkOutTime,
    Boolean soldOut,
    Integer count,
    List<CouponResponse> coupons,
    List<String> images,
    RoomOptionResponse roomOption
) {

    public static RoomResponse from(
        Room room, Integer discountPrice, Boolean soldOut,
        Integer couponRoomPrice
    ) {
        return RoomResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .basePrice(
                Optional.ofNullable(room.getRoomPrice())
                    .map(roomPrice -> Stream.of(
                            roomPrice.getOffWeekDaysMinFee(),
                            roomPrice.getOffWeekendMinFee(),
                            roomPrice.getPeakWeekDaysMinFee(),
                            roomPrice.getPeakWeekendMinFee())
                        .reduce(Math::min).orElse(0))
                    .orElse(0)
            )
            .discountPrice(discountPrice)
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .soldOut(soldOut)
            .count(room.getCount())
            .coupons(
                Optional.ofNullable(room.getCouponRooms())
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(couponRoom -> CouponResponse.fromRoom(couponRoom.getCoupon(), couponRoomPrice))
                    .toList()
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
