package com.backoffice.upjuyanolja.domain.openapi.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InvalidDataException extends ApplicationException {

    public InvalidDataException() {
        super(ErrorCode.DATA_NOT_FOUND);
    }
}
