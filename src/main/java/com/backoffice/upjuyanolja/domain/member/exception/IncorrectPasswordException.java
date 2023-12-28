package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class IncorrectPasswordException extends ApplicationException {

    public IncorrectPasswordException() {
        super(ErrorCode.INCORRECT_PASSWORD);
    }
}
