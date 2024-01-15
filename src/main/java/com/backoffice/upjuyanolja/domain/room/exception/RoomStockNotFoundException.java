package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class RoomStockNotFoundException extends ApplicationException {

    public RoomStockNotFoundException() {
        super(ErrorCode.ROOM_NOT_FOUND);
    }
}
