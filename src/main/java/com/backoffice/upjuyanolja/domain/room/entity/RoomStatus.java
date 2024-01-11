package com.backoffice.upjuyanolja.domain.room.entity;

import lombok.Getter;

@Getter
public enum RoomStatus {

    SELLING("판매 중"),
    STOP_SELLING("판매 중지");

    private final String name;

    RoomStatus(String name) {
        this.name = name;
    }
}
