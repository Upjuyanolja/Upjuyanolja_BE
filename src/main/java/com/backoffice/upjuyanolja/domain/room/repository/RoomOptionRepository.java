package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomOptionRepository extends JpaRepository<RoomOption, Long> {

    Optional<RoomOption> findByRoom(Room room);

}
