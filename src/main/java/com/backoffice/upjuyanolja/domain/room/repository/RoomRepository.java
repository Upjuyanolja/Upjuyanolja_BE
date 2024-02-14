package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 객실 Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface RoomRepository extends JpaRepository<Room, Long>, RoomCustomRepository {

}
