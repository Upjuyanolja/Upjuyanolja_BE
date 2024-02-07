package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {

    List<RoomImage> findByRoom(Room room);

}
