package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class MemberEmailDuplicationException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.DUPLICATED_EMAIL;


    @Override
    public ErrorCode getErrorCode() {
        return super.getErrorCode();
    }

    public MemberEmailDuplicationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
