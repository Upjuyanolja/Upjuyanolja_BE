package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 객실 옵션 Repository Interface
 *
 * @author HyunA (vikim1210@naver.com)
 */
public interface RoomOptionRepository extends JpaRepository<RoomOption, Long> {

    /**
     * 객실에 해당하는 객실 옵션 조회 메서드
     *
     * @param room 객실 옵션을 조회할 객실 Entity
     * @return 객실 옵션 Entity
     * @author HyunA (vikim1210@naver.com)
     */
    Optional<RoomOption> findByRoom(Room room);
}
