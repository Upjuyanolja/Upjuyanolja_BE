package com.backoffice.upjuyanolja.global.exception;

import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.FailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<FailResponse> ApplicationException(ApplicationException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(e.getErrorCode())
            .message(e.getErrorCode().getMessage())
            .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<FailResponse> bindException(BindException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.INVALID_DATE)
            .message(e.getMessage())
            .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<FailResponse> dbException(DataAccessException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.DATABASE_ERROR)
            .message(ErrorCode.DATABASE_ERROR.getMessage())
            .build()
        );
    }

    @ExceptionHandler
    public ResponseEntity<FailResponse> serverException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.SERVER_ERROR)
            .message(ErrorCode.SERVER_ERROR.getMessage())
            .build()
        );
    }
}
