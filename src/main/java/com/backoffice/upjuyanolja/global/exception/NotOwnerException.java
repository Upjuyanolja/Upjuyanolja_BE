package com.backoffice.upjuyanolja.global.exception;

/**
 * 업주 회원이 아닌 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class NotOwnerException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public NotOwnerException() {
        super(ErrorCode.NOT_OWNER);
    }
}
