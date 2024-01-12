package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    public static RoomResponse of(
        Room room, int discountPrice, boolean soldOut,
        CouponRoomDetailResponse couponRoomDetailResponse

    ) {
        return RoomResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .basePrice(
                Stream.of(
                        room.getPrice().getOffWeekDaysMinFee(),
                        room.getPrice().getOffWeekendMinFee(),
                        room.getPrice().getPeakWeekDaysMinFee(),
                        room.getPrice().getPeakWeekendMinFee())
                    .reduce(Math::min).orElse(0)
            )
            .discountPrice(discountPrice)
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .soldOut(soldOut)
            .count(room.getStocks().size())
            .coupons(
                Stream.of(couponRoomDetailResponse)
                    .filter(response -> response.roomName().equals(room.getName()))
                    .map(response -> response.couponRooms())
                    .flatMap(List::stream)
                    .toList()
            )
            .images(
                room.getImages().stream()
                    .map(image -> image.getUrl())
                    .toList()
            )
            .roomOption(RoomOptionResponse.of(room.getOption()))
            .build();
    }

}
