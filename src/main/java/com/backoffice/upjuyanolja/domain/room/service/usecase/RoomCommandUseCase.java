package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import java.time.LocalDate;
import java.util.List;

public interface RoomCommandUseCase {

    RoomInfoResponse registerRoom(long memberId, long accommodationId, RoomRegisterRequest request);

    RoomInfoResponse saveRoom(Accommodation accommodation, RoomRegisterRequest request);

    List<RoomStock> getFilteredRoomStocksByDate(Room room, LocalDate startDate, LocalDate endDate);

    RoomInfoResponse modifyRoom(long memberId, long roomId, RoomUpdateRequest request);

    @Builder
    record RoomUpdate(
        String name,
        String status,
        int price,
        int defaultCapacity,
        int maxCapacity,
        String checkInTime,
        String checkOutTime,
        int amount,
        RoomOptionUpdate option
    ) {

    }

    @Builder
    record RoomOptionUpdate(
        boolean airCondition,
        boolean tv,
        boolean internet
    ) {

    }
}
