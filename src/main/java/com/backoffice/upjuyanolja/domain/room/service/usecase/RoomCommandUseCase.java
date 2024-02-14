package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import lombok.Builder;

/**
 * 객실 생성, 수정, 삭제 UseCase Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface RoomCommandUseCase {

    /**
     * 객실 등록 메서드
     *
     * @param memberId            객실을 등록하는 업주 회원 식별자
     * @param accommodationId     객실을 등록할 숙소 식별자
     * @param roomRegisterRequest 객실 등록 요청 DTO
     * @return 등록한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    RoomInfoResponse registerRoom(
        long memberId,
        long accommodationId,
        RoomRegisterRequest roomRegisterRequest
    );

    /**
     * 객실 정보 수정 메서드
     *
     * @param memberId          객실 정보를 수정하는 업주 회원 식별자
     * @param roomId            정보를 수정할 객실 식별자
     * @param roomUpdateRequest 객실 정보 수정 요청 DTO
     * @return 수정한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    RoomInfoResponse modifyRoom(
        long memberId,
        long roomId,
        RoomUpdateRequest roomUpdateRequest
    );

    /**
     * 객실 삭제 메서드
     *
     * @param memberId 객실을 삭제하는 업주 회원 식별자
     * @param roomId   삭제할 객실 식별자
     * @return 삭제한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    RoomInfoResponse deleteRoom(long memberId, long roomId);

    /**
     * 객실 수정 DTO Record
     *
     * @param name            수정할 객실 이름
     * @param status          수정할 객실 상태
     * @param price           수정할 객실 가격
     * @param defaultCapacity 수정할 객실 기본 인원
     * @param maxCapacity     수정할 객실 최대 인원
     * @param checkInTime     수정할 객실 체크인 시간
     * @param checkOutTime    수정할 객실 체크아웃 시간
     * @param amount          수정할 객실 개수
     * @param option          객실 옵션 수정 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    record RoomUpdateDto(
        String name,
        String status,
        int price,
        int defaultCapacity,
        int maxCapacity,
        String checkInTime,
        String checkOutTime,
        int amount,
        RoomOptionUpdateDto option
    ) {

    }

    /**
     * 객실 옵션 수정 DTO Record
     *
     * @param airCondition 수정할 객실 에어컨 여부
     * @param tv           수정할 객실 TV 여부
     * @param internet     수정할 객실 인터넷 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    record RoomOptionUpdateDto(
        boolean airCondition,
        boolean tv,
        boolean internet
    ) {

    }
}
