package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class CreateVerificationCodeException extends ApplicationException {

    public CreateVerificationCodeException() {
        super(ErrorCode.SERVER_ERROR);
    }
}
