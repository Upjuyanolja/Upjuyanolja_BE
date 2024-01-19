package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class FailedSaveImageException extends ApplicationException {

    public FailedSaveImageException() {
        super(ErrorCode.FAILED_SAVE_IMAGE);
    }
}
