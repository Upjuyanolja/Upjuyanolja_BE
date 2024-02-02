package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface RoomQueryUseCase {

    Room findRoomById(long roomId);

    List<Room> findByAccommodationId(long accommodationId);

    RoomOption findRoomOptionByRoom(Room room);

    RoomPrice findRoomPriceByRoom(Room room);

    RoomPageResponse getRooms(long memberId, long accommodationId, Pageable pageable);

    RoomInfoResponse getRoom(long memberId, long roomId);

    List<RoomStock> getFilteredRoomStocksByDate(Room room, LocalDate startDate, LocalDate endDate);

    Map<Room, Integer> getMinRoomPriceWithRoom(List<Room> rooms);

}
