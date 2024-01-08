package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class WrongAccommodationTypeException extends ApplicationException {

    public WrongAccommodationTypeException() {
        super(ErrorCode.WRONG_ACCOMMODATION_TYPE);
    }
}
