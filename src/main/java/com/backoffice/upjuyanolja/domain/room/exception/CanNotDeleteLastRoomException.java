package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class CanNotDeleteLastRoomException extends ApplicationException {

    public CanNotDeleteLastRoomException() {
        super(ErrorCode.LAST_ROOM);
    }
}
