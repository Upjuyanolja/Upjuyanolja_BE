package com.backoffice.upjuyanolja.domain.reservation.exception;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.INVALID_COUPON;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;

public class InvalidCouponException extends ApplicationException {

    public InvalidCouponException() {
        super(INVALID_COUPON);
    }
}
