package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class AuthorizeInfoNotFoundException extends ApplicationException {

    public AuthorizeInfoNotFoundException() {
        super(ErrorCode.AUTHORIZE_INFO_NOT_FOUND);
    }
}
