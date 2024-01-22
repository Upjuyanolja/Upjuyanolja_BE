package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class AccommodationImageNotExistsException extends ApplicationException {

    public AccommodationImageNotExistsException() {
        super(ErrorCode.ACCOMMODATION_IMAGE_NOT_EXISTS);
    }
}
