package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class AccommodationException extends ApplicationException {

    public AccommodationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
