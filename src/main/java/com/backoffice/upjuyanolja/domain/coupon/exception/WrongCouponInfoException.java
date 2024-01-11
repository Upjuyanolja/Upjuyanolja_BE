package com.backoffice.upjuyanolja.domain.coupon.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class WrongCouponInfoException extends ApplicationException {

    public WrongCouponInfoException() {
        super(ErrorCode.WRONG_COUPON_INFO);
    }
}
