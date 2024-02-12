package com.backoffice.upjuyanolja.domain.room.entity;

import lombok.Getter;

/**
 * 객실 상태 Enum
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
public enum RoomStatus {

    /**
     * 객실 판매 중 상태
     */
    SELLING("판매 중"),

    /**
     * 객실 판매 중지 상태
     */
    STOP_SELLING("판매 중지");

    /**
     * 객실 상태 이름
     */
    private final String name;

    /**
     * All Arguments Constructor
     *
     * @param name 객실 상태 이름
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    RoomStatus(String name) {
        this.name = name;
    }
}
