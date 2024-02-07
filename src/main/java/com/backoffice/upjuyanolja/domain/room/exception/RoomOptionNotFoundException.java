package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class RoomOptionNotFoundException extends ApplicationException {

    public RoomOptionNotFoundException() {
        super(ErrorCode.ROOM_OPTION_NOT_FOUND);
    }
}
