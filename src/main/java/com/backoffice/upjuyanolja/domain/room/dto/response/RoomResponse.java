package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponIssuanceResponse;
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
    List<CouponDetailResponse> coupons,
    List<String> images,
    RoomOptionResponse roomOption
) {

    public static RoomResponse of(
        Room room, int discountPrice, boolean soldOut, int count,
        List<CouponDetailResponse> coupons

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
            .defaultCapacity(room.getStandard())
            .maxCapacity(room.getCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .soldOut(soldOut)
            .count(count)
            .coupons(coupons)
            .images(
                room.getImages().stream()
                    .map(image -> image.getUrl())
                    .toList()
            )
            .roomOption(RoomOptionResponse.of(room.getOption()))
            .build();
    }

}
