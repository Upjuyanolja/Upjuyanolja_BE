package com.backoffice.upjuyanolja.domain.member.exception;

import com.backoffice.upjuyanolja.global.exception.ApplicationException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;

/**
 * 회원을 찾을 수 없는 경우 던질 Exception Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class MemberNotFoundException extends ApplicationException {

    /**
     * No Arguments Constructor
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
