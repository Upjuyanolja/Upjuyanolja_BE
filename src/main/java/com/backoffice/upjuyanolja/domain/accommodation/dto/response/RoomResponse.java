package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Room;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        CouponRoomDetailResponse couponRoomDetailResponse

    ) {
        return RoomResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .basePrice(
                Stream.of(
                        room.getRoomPrice().getOffWeekDaysMinFee(),
                        room.getRoomPrice().getOffWeekendMinFee(),
                        room.getRoomPrice().getPeakWeekDaysMinFee(),
                        room.getRoomPrice().getPeakWeekendMinFee())
                    .reduce(Math::min).orElse(0)
            )
            .discountPrice(discountPrice)
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .soldOut(soldOut)
            .count(room.getCount())
            .coupons(
                Stream.of(couponRoomDetailResponse)
                    .filter(response -> response.roomName().equals(room.getName()))
                    .map(response -> response.couponRooms())
                    .flatMap(List::stream)
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
