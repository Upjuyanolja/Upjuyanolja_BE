package com.backoffice.upjuyanolja.domain.coupon.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InvalidCouponInfoException extends ApplicationException {

    public InvalidCouponInfoException() {
        super(ErrorCode.INVALID_COUPON_INFO);
    }
}
