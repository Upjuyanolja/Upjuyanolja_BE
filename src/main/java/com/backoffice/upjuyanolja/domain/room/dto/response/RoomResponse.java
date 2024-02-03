package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
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
    List<CouponDetailResponse> coupons,
    List<String> images,
    RoomOptionResponse roomOption
) {

    public static RoomResponse of(
        Room room, RoomOption option, int roomPrice,
        int discountPrice, boolean soldOut, int count,
        List<String> images, List<CouponDetailResponse> coupons

    ) {
        return RoomResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .basePrice(roomPrice)
            .discountPrice(discountPrice)
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .soldOut(soldOut)
            .count(count)
            .coupons(coupons)
            .images(images)
            .roomOption(RoomOptionResponse.of(option))
            .build();
    }

}
