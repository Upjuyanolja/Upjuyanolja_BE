package com.backoffice.upjuyanolja.domain.room.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;

public interface RoomCommandUseCase {

    RoomInfoResponse registerRoom(long memberId, long accommodationId, RoomRegisterRequest request);

    RoomInfoResponse saveRoom(Accommodation accommodation, RoomRegisterRequest request);
}
