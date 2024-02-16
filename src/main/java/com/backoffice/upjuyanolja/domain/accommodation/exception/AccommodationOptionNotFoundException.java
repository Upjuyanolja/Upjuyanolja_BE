package com.backoffice.upjuyanolja.domain.accommodation.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 숙소 옵션을 찾지 못한 경우 던질 Exception Class
 *
 * @author HyunA (vikim1210@naver.com)
 */
public class AccommodationOptionNotFoundException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author HyunA (vikim1210@naver.com)
     */
    public AccommodationOptionNotFoundException() {
        super(ErrorCode.ACCOMMODATION_OPTION_NOT_FOUND);
    }
}
