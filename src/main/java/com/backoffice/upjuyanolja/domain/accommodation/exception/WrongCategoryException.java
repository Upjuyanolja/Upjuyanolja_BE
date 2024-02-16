package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 카테고리가 알맞지 않는 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class WrongCategoryException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public WrongCategoryException() {
        super(ErrorCode.WRONG_CATEGORY);
    }
}
