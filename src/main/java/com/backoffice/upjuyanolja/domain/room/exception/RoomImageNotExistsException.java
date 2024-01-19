package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class RoomImageNotExistsException extends ApplicationException {

    public RoomImageNotExistsException() {
        super(ErrorCode.ROOM_IMAGE_NOT_EXISTS);
    }
}
