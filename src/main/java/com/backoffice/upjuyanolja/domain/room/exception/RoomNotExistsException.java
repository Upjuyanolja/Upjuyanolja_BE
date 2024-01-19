package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class RoomNotExistsException extends ApplicationException {

    public RoomNotExistsException() {
        super(ErrorCode.ROOM_NOT_EXISTS);
    }
}
