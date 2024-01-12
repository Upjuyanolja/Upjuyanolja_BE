package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

public interface RoomQueryUseCase {

    Room saveRoom(Accommodation accommodation, Room request);

    List<RoomImage> saveRoomImages(List<RoomImage> request);

    Room getRoomById(long id);

    boolean existsRoomByName(String name);

    @Builder
    record RoomSaveRequest(
        String name,
        RoomPrice price,
        int defaultCapacity,
        int maxCapacity,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        int amount,
        RoomOption option
    ) {

    }
}
