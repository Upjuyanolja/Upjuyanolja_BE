package com.backoffice.upjuyanolja.domain.room.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RoomPageResponse(
    int pageNum,
    int pageSize,
    int totalPages,
    long totalElements,
    boolean isLast,
    List<RoomsInfoResponse> rooms
) {

}
