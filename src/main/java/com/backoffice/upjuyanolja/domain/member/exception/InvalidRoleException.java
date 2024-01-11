package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class InvalidRoleException extends ApplicationException {

    public InvalidRoleException() {
        super(ErrorCode.INVALID_ROLE);
    }
}
