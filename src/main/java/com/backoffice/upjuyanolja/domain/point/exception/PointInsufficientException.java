package com.backoffice.upjuyanolja.domain.point.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class PointInsufficientException extends ApplicationException {

    public PointInsufficientException() {
        super(ErrorCode.POINT_INSUFFICIENT);
    }
}
