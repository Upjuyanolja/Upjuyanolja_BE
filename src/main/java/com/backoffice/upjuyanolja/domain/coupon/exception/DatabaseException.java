package com.backoffice.upjuyanolja.domain.coupon.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class DatabaseException extends ApplicationException {

    public DatabaseException() {
        super(ErrorCode.DATABASE_ERROR);
    }
}
