package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class MemberEmailDuplicationException extends ApplicationException {

    public MemberEmailDuplicationException() {
        super(ErrorCode.DUPLICATED_EMAIL);
    }
}
