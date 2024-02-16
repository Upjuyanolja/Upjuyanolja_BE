package com.backoffice.upjuyanolja.domain.image.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 이미지 저장에 실패한 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class FailedSaveImageException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public FailedSaveImageException() {
        super(ErrorCode.FAILED_SAVE_IMAGE);
    }
}
