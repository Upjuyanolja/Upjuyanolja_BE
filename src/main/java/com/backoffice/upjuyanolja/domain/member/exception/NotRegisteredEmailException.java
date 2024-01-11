package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class NotRegisteredEmailException extends ApplicationException {

    public NotRegisteredEmailException() {
        super(ErrorCode.NOT_REGISTERED_EMAIL);
    }
}
