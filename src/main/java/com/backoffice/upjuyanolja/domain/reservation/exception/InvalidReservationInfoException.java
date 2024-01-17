package com.backoffice.upjuyanolja.domain.reservation.exception;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.INVALID_RESERVATION_INFO;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;

public class InvalidReservationInfoException extends ApplicationException {

    public InvalidReservationInfoException() {
        super(INVALID_RESERVATION_INFO);
    }
}
