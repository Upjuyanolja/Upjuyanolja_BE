package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InvalidTimeFormatException extends ApplicationException {

    public InvalidTimeFormatException() {
        super(ErrorCode.INVALID_TIME_FORMAT);
    }
}
