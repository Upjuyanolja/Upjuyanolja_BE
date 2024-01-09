package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class DuplicateRoomNameException extends ApplicationException {

    public DuplicateRoomNameException() {
        super(ErrorCode.DUPLICATED_ROOM_NAME);
    }
}
