package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class AccommodationOptionNotFoundException extends ApplicationException {

    public AccommodationOptionNotFoundException() {
        super(ErrorCode.ACCOMMODATION_OPTION_NOT_FOUND);
    }
}
