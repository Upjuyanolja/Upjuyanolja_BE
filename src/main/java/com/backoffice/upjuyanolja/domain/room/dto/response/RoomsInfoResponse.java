package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import lombok.Builder;

@Builder
public record RoomsInfoResponse(
    long id,
    String name,
    int defaultCapacity,
    int maxCapacity,
    String checkInTime,
    String checkOutTime,
    int basePrice,
    int discountPrice,
    int amount,
    String status,
    List<RoomImageResponse> images,
    RoomOptionResponse option,
    List<CouponDetailResponse> coupons
) {

    public static RoomsInfoResponse of(Room room, List<CouponDetailResponse> coupons) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> i));
        for (CouponDetailResponse coupon : coupons) {
            pq.offer(coupon.price());
        }
        int discountPrice = pq.isEmpty() ? room.getPrice().getOffWeekDaysMinFee() : pq.poll();

        return RoomsInfoResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .basePrice(room.getPrice().getOffWeekDaysMinFee())
            .discountPrice(discountPrice)
            .amount(room.getAmount())
            .status(room.getStatus().name())
            .images(RoomImageResponse.of(room.getImages()))
            .option(RoomOptionResponse.of(room.getOption()))
            .coupons(coupons)
            .build();
    }
}
