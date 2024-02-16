package com.backoffice.upjuyanolja.domain.room.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 마지막 남은 객실을 삭제하려고 하는 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class CanNotDeleteLastRoomException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public CanNotDeleteLastRoomException() {
        super(ErrorCode.LAST_ROOM);
    }
}
