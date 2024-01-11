package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

public class WrongCategoryException extends ApplicationException {

    public WrongCategoryException() {
        super(ErrorCode.WRONG_CATEGORY);
    }
}
