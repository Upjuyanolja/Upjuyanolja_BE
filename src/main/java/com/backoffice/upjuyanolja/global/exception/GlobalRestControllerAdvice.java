package com.backoffice.upjuyanolja.global.exception;

import com.backoffice.upjuyanolja.global.common.ApiResponse;
import com.backoffice.upjuyanolja.global.common.ApiResponse.FailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<FailResponse> ApplicationException(ApplicationException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(e.getErrorCode().getCode())
            .message(e.getErrorCode().getMessage())
            .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<FailResponse> bindException(BindException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.INVALID_DATE.getCode())
            .message(e.getMessage())
            .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<FailResponse> dbException(DataAccessException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.DATABASE_ERROR.getCode())
            .message(ErrorCode.DATABASE_ERROR.getMessage())
            .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<FailResponse> serverException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.SERVER_ERROR.getCode())
            .message(ErrorCode.SERVER_ERROR.getMessage())
            .build()
        );
    }

    /**
     * 유효하지 않은 양식의 이메일(@ 빠짐)이나 유효하지 않은 비밀번호 (특수문자 미포함)로 로그인을 시도한경우 validation에 잡혀서
     * methodArgumentNotValidException이 발생합니다. methodArgumentNotValidException은 BindException을 상속받고
     * 있어서 INVALID_DATE 에러코드가 출력되기 떄문에 methodArgumentNotValidException를 직접 처리하는 핸들러를 정의했습니다. 에러코드는
     * COMMON으로 정의된 INVALID_REQUEST_BODY를 사용했습니다.
     */
    @ExceptionHandler
    public ResponseEntity<FailResponse> methodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.INVALID_REQUEST_BODY.getCode())
            .message(ErrorCode.INVALID_REQUEST_BODY.getMessage())
            .build()
        );
    }
}
