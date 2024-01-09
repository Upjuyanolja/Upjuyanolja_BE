package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class SendEmailErrorException extends ApplicationException {

    public SendEmailErrorException() {
        super(ErrorCode.EMAIL_SEND_ERROR);
    }
}
