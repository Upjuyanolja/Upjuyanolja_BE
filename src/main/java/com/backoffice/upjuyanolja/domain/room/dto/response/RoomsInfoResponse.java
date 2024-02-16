package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.PriorityQueue;
import lombok.Builder;

/**
 * 객실 정보 목록 조회 응답 DTO Record
 *
 * @param id              객실 식별자
 * @param name            객실 이름
 * @param defaultCapacity 객실 기본 인원
 * @param maxCapacity     객실 최대 인원
 * @param checkInTime     객실 체크인 시간
 * @param checkOutTime    객실 체크아웃 시간
 * @param basePrice       객실 기본 가격
 * @param discountPrice   객실 할인 가격
 * @param amount          객실 개수
 * @param status          객실 상태
 * @param images          객실 이미지 응답 DTO 리스트
 * @param option          객실 옵션 응답 DTO
 * @param coupons         객실에 등록된 쿠폰 상세 응답 DTO 리스트
 * @author JeongUijeong (jeong275117@gmail.com)
 */
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

    /**
     * 객실 Entity, 객실 옵션 Entity, 객실 이미지 Entity 리스트, 객실에 등록한 쿠폰 상세 응답 DTO 리스트, 객실 가격으로 객실 목록 정보 응답
     * DTO를 생성하는 정적 팩토리 메서드
     *
     * @param room      객실 Entity
     * @param option    객실 옵션 Entity
     * @param images    객실 이미지 Entity 리스트
     * @param coupons   객실에 등록한 쿠폰 상세 응답 DTO 리스트
     * @param roomPrice 객실 가격
     * @return 객실 목록 정보 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
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
            .status(room.getStatus().name())
            .amount(room.getAmount())
            .basePrice(roomPrice)
            .discountPrice(discountPrice)
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .option(RoomOptionResponse.of(option))
            .images(RoomImageResponse.of(images))
            .coupons(coupons)
            .build();
    }
}
