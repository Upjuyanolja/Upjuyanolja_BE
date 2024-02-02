package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class RoomPriceNotFoundException extends ApplicationException {

    public RoomPriceNotFoundException() {
        super(ErrorCode.ROOM_PRICE_NOT_FOUND);
    }
}
