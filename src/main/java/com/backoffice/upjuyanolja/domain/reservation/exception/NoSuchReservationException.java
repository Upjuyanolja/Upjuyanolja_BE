package com.backoffice.upjuyanolja.domain.reservation.exception;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.RESERVATION_NOT_FOUND;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;

public class NoSuchReservationException extends ApplicationException {

  public NoSuchReservationException() {
    super(RESERVATION_NOT_FOUND);
  }
}
