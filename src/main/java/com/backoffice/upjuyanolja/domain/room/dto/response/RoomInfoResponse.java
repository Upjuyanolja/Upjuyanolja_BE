package com.backoffice.upjuyanolja.domain.room.dto.response;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;

/**
 * 객실 정보 응답 DTO Record
 *
 * @param name            객실 이름
 * @param price           객실 가격
 * @param defaultCapacity 객실 기본 인원
 * @param maxCapacity     객실 최대 인원
 * @param checkInTime     객실 체크인 시간
 * @param checkOutTime    객실 체크아웃 시간
 * @param amount          객실 개수
 * @param images          객실 이미지 등록 요청 DTO 리스트
 * @param option          객실 옵션 등록 요청 DTO
 * @author JeongUijeong (jeong275117@gmail.com)
 */
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

    /**
     * 객실 Entity, 객실 옵션 Entity, 객실 이미지 Entity 리스트, 객실 가격으로 객실 정보 응답 DTO를 생성하는 정적 팩토리 메서드
     *
     * @param room   객실 Entity
     * @param option 객실 옵션 Entity
     * @param images 객실 이미지 Entity 리스트
     * @param price  객실 가격
     * @return 객실 정보 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static RoomInfoResponse of(
        Room room,
        RoomOption option,
        List<RoomImage> images,
        int price
    ) {
        return RoomInfoResponse.builder()
            .id(room.getId())
            .name(room.getName())
            .status(room.getStatus().name())
            .amount(room.getAmount())
            .price(price)
            .defaultCapacity(room.getDefaultCapacity())
            .maxCapacity(room.getMaxCapacity())
            .checkInTime(room.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .checkOutTime(room.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm")))
            .option(RoomOptionResponse.of(option))
            .images(RoomImageResponse.of(images))
            .build();
    }
}
