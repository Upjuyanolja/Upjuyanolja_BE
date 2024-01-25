package com.backoffice.upjuyanolja.domain.point.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class PaymentAuthorizationFailedException extends ApplicationException {

    public PaymentAuthorizationFailedException() {
        super(ErrorCode.PAYMENT_AUTHORIZATION_FAILED);
    }
}
