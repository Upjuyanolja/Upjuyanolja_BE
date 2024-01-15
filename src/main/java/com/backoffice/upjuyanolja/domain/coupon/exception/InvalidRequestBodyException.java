package com.backoffice.upjuyanolja.domain.coupon.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InvalidRequestBodyException extends ApplicationException {

    public InvalidRequestBodyException() {
        super(ErrorCode.INVALID_REQUEST_BODY);
    }
}
