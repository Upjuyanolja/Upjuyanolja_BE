package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import java.util.List;

/**
 * 객실 재고 Custom Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface RoomStockCustomRepository {

    /**
     * 특정 객실에 대한 오늘을 포함한 이후 객실 재고를 조회하는 메서드
     *
     * @param room 객실 재고를 조회할 객실 Entity
     * @return 객실 재고 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    List<RoomStock> findAllByRoomAndAfterToday(Room room);
}
