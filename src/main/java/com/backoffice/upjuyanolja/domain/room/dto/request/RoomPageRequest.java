package com.backoffice.upjuyanolja.domain.room.dto.request;

import lombok.Builder;

@Builder
public record RoomPageRequest(int pageNum, int pageSize) {

    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(pageNum, pageSize);
    }
}
