package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 객실 재고 Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface RoomStockRepository extends JpaRepository<RoomStock, Long>, RoomStockCustomRepository {

    /**
     * 특정 객실의 객실 재고 Entity 리스트를 조회하는 메서드
     *
     * @param room 객실 재고를 조회할 객실 Entity
     * @return 객실 재고 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    List<RoomStock> findAllByRoom(Room room);
}
