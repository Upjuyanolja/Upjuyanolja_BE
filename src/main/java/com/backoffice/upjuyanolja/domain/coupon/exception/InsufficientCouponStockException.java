package com.backoffice.upjuyanolja.domain.coupon.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InsufficientCouponStockException extends ApplicationException {

    public InsufficientCouponStockException() {
        super(ErrorCode.INSUFFICIENT_COUPON_STOCK);
    }
}
