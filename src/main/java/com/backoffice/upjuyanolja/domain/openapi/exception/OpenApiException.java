package com.backoffice.upjuyanolja.domain.openapi.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class OpenApiException extends ApplicationException {

    public OpenApiException() {
        super(ErrorCode.OPEN_API_ERROR);
    }
}
