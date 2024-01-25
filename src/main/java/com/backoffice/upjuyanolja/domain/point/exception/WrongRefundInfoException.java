package com.backoffice.upjuyanolja.domain.point.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class WrongRefundInfoException extends ApplicationException {

    public WrongRefundInfoException() {
        super(ErrorCode.WRONG_REFUND_INFO);
    }
}
