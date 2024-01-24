package com.backoffice.upjuyanolja.domain.point.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class TossApiErrorException extends ApplicationException {

    public TossApiErrorException() {
        super(ErrorCode.TOSS_API_ERROR);
    }
}
