package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 객실 Custom Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface RoomCustomRepository {

    /**
     * 숙소 식별자와 Pageable 객체를 받아 해당 숙소 소속의 객실 목록을 조회하는 메서드
     *
     * @param accommodationId 객실 목록을 조회하고자 하는 숙소 식별자
     * @param pageable        페이지네이션 조건을 담은 Pageable 객체
     * @return 객실 목록 페이지
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    Page<Room> findAllByAccommodationId(long accommodationId, Pageable pageable);

    /**
     * 숙소 식별자로 해당 숙소 소속의 객실 목록을 조회하는 메서드
     *
     * @param accommodationId 객실 목록을 조회하고자 하는 숙소 식별자
     * @return 객실 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    List<Room> findAllByAccommodationId(long accommodationId);

    /**
     * 숙소 내 객실 이름 중복 여부 확인 메서드
     *
     * @param name          중복 여부를 확인할 객실 이름
     * @param accommodation 중복 여부를 확인할 객실이 속한 숙소 Entity
     * @return 숙소 내 객실 이름 중복 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    boolean existsRoomByNameAndAccommodation(String name, Accommodation accommodation);

    /**
     * 숙소에 속한 객실 개수 조회 메서드
     *
     * @param accommodationId 객실 개수를 조회할 숙소 식별자
     * @return 객실 개수
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    long countByAccommodationId(long accommodationId);
}
