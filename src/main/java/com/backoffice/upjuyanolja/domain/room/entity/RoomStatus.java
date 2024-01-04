package com.backoffice.upjuyanolja.domain.room.entity;

import lombok.Getter;

@Getter
public enum RoomStatus {

    ISSUING("발급 중"),
    SUSPENDED("발급 중지"),
    EXHAUSTED("소진");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

}
