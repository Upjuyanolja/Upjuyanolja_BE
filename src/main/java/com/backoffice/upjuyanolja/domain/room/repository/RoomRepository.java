package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomCustomRepository {

    List<Room> findByAccommodationId(long id);
}
