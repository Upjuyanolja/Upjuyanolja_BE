package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class IncorrectVerificationCodeException extends ApplicationException {

    public IncorrectVerificationCodeException() {
        super(ErrorCode.INCORRECT_VERIFICATION_CODE);
    }
}
