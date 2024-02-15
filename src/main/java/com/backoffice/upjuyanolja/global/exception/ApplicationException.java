package com.backoffice.upjuyanolja.global.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 상 발생하는 Exception Class
 * <p>
 * 비즈니스 로직에서 발생는 예외와 다른 다양한 예외들을 쉽게 구분하기 위해 사용하는 Exception Class입니다. <br> 다양한 Exception Class에서 상속받아
 * 사용하고 있는 RuntimeException을 바로 상속하지 않고, 비즈니스 로직 상 발생하는 Exception의 경우엔 ApplicationException을 상속받도록
 * 합니다.
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    /**
     * Custom ErrorCode
     */
    private final ErrorCode errorCode;

    /**
     * ErrorCode를 매개변수로 받는 생성자
     *
     * @param errorCode Custom ErrorCode
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    protected ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
