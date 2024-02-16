package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 숙소 이미지가 존재하지 않는 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class AccommodationImageNotExistsException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public AccommodationImageNotExistsException() {
        super(ErrorCode.ACCOMMODATION_IMAGE_NOT_EXISTS);
    }
}
