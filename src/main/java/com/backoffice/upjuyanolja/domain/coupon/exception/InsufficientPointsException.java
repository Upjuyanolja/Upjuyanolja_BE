package com.backoffice.upjuyanolja.domain.coupon.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InsufficientPointsException extends ApplicationException {

    public InsufficientPointsException() {
        super(ErrorCode.POINT_INSUFFICIENT);
    }
}
