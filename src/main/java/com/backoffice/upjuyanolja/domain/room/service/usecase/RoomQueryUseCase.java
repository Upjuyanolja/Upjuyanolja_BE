package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * 객실 조회 UseCase Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface RoomQueryUseCase {

    /**
     * 객실 목록 조회 메서드
     *
     * @param memberId        객실 목록을 조회할 업주 회원 식별자
     * @param accommodationId 객실 목록을 조회하고자 하는 숙소 식별자
     * @param pageable        객실 페이지네이션 조회를 위한 Pageable 객체
     * @return 객실 정보 목록 페이지
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    RoomPageResponse getRooms(
        long memberId,
        long accommodationId,
        Pageable pageable
    );

    /**
     * 객실 상세 조회 메서드
     *
     * @param memberId 객실 상세 정보를 조회할 업주 회원 식별자
     * @param roomId   상세 정보를 조회하고자 하는 객실 식별자
     * @return 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    RoomInfoResponse getRoom(long memberId, long roomId);
}
