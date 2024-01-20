package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InvalidSignupProcessException extends ApplicationException {

    public InvalidSignupProcessException() {
        super(ErrorCode.INVALID_SIGNUP_PROCESS);
    }
}
