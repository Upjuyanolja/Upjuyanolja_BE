package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 유효하지 않은 시간 형태로 객실 체크인/체크아웃 등록/수정을 요청한 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class InvalidTimeFormatException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public InvalidTimeFormatException() {
        super(ErrorCode.INVALID_TIME_FORMAT);
    }
}
