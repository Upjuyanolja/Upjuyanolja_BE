package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 객실 이미지 Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {

    /**
     * 객실에 해당하는 객실 이미지 리스트 조회 메서드
     *
     * @param room 객실 이미지를 조회할 객실 Entity
     * @return 객실 이미지 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    List<RoomImage> findByRoom(Room room);
}
