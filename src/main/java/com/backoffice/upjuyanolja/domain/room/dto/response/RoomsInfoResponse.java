package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import java.time.format.DateTimeFormatter;
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

    public static RoomsInfoResponse of(
        Room room, RoomOption option, List<RoomImage> images,
        List<CouponDetailResponse> coupons, int roomPrice
    ) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (CouponDetailResponse coupon : coupons) {
            pq.offer(coupon.price());
        }
        int discountPrice = pq.isEmpty() ? roomPrice : pq.poll();

        return RoomsInfoResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .basePrice(roomPrice)
            .discountPrice(discountPrice)
            .amount(room.getAmount())
            .status(room.getStatus().name())
            .images(RoomImageResponse.of(images))
            .option(RoomOptionResponse.of(option))
            .coupons(coupons)
            .build();
    }
}
