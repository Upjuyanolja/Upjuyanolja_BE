package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class LoggedOutMemberException extends ApplicationException {

    public LoggedOutMemberException() {
        super(ErrorCode.LOGGED_OUT);
    }
}
