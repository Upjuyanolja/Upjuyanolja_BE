package com.backoffice.upjuyanolja.domain.reservation.exception;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.RESERVATION_ROOM_NOT_FOUND;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;

public class NoSuchReservationRoomException extends ApplicationException {

    public NoSuchReservationRoomException() {
        super(RESERVATION_ROOM_NOT_FOUND);
    }
}
