package com.backoffice.upjuyanolja.global.exception;

public class NotOwnerException extends ApplicationException {

    public NotOwnerException() {
        super(ErrorCode.NOT_OWNER);
    }
}
