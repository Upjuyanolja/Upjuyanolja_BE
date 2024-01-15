package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InvalidRoomStatusException extends ApplicationException {

    public InvalidRoomStatusException() {
        super(ErrorCode.INVALID_ROOM_STATUS);
    }
}
