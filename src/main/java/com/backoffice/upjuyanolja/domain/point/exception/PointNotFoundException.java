package com.backoffice.upjuyanolja.domain.point.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class PointNotFoundException extends ApplicationException {

    public PointNotFoundException() {
        super(ErrorCode.POINT_NOT_FOUND);
    }
}
