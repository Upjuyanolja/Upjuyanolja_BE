package com.backoffice.upjuyanolja.domain.point.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class PointTradeFailedException extends ApplicationException {

    public PointTradeFailedException() {
        super(ErrorCode.POINT_TRADE_FAILED);
    }
}
