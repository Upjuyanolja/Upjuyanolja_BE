package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 객실 가격을 찾지 못한 경우 던질 Exception Class
 *
 * @author HyunA (vikim1210@naver.com)
 */
public class RoomPriceNotFoundException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author HyunA (vikim1210@naver.com)
     */
    public RoomPriceNotFoundException() {
        super(ErrorCode.ROOM_PRICE_NOT_FOUND);
    }
}
