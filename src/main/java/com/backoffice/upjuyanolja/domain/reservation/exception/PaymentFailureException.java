package com.backoffice.upjuyanolja.domain.reservation.exception;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.RESERVATION_PAYMENT_FAILED;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;

public class PaymentFailureException extends ApplicationException {

  public PaymentFailureException() {
    super(RESERVATION_PAYMENT_FAILED);
  }
}
