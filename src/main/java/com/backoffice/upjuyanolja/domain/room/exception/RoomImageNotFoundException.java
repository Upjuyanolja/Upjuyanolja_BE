package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class RoomImageNotFoundException extends ApplicationException {

    public RoomImageNotFoundException() {
        super(ErrorCode.ROOM_IMAGE_NOT_FOUND);
    }
}
