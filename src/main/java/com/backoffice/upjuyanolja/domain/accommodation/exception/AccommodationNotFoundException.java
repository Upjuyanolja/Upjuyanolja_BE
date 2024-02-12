package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 숙소를 찾지 못한 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class AccommodationNotFoundException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public AccommodationNotFoundException() {
        super(ErrorCode.ACCOMMODATION_NOT_FOUND);
    }
}
