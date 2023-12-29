package com.backoffice.upjuyanolja.domain.coupon.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class CouponException extends ApplicationException {

    public CouponException(ErrorCode errorCode) {
        super(errorCode);
    }
}
