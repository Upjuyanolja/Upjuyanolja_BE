package com.backoffice.upjuyanolja.global.exception;

import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.FailResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역적으로 발생하는 Exception을 핸들링 하기 위한 Controller Advice Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    /**
     * 비즈니스 로직에서 발생하는 ApplicationException 핸들러
     *
     * @param e ApplicationException
     * @return 에러 응답을 담은 ResponseEntity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @ExceptionHandler
    public ResponseEntity<FailResponse> ApplicationException(ApplicationException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(e.getErrorCode().getCode())
            .message(e.getErrorCode().getMessage())
            .build()
        );
    }

    /**
     * Validation에 실패한 경우 발생하는 BindException 핸들러
     *
     * @param e BindException
     * @return 에러 응답을 담은 ResponseEntity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @ExceptionHandler
    public ResponseEntity<FailResponse> bindException(BindException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.INVALID_REQUEST_BODY.getCode())
            .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
            .build()
        );
    }

    /**
     * DB 접근 시에 발생하는 DataAccessException 핸들러
     *
     * @param e DataAccessException
     * @return 에러 응답을 담은 ResponseEntity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @ExceptionHandler
    public ResponseEntity<FailResponse> dbException(DataAccessException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.DATABASE_ERROR.getCode())
            .message(ErrorCode.DATABASE_ERROR.getMessage())
            .build()
        );
    }

    /**
     * 그 외 발생하는 RuntimeException 핸들러
     * <p>
     * 그 외 핸들러로 잡히지 않은 런타임 Exception의 경우엔 알 수 없는 서버 에러로 처리합니다.
     *
     * @param e RuntimeException
     * @return 에러 응답을 담은 ResponseEntity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
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
     *
     * @param e MethodArgumentNotValidException
     * @return 에러 응답을 담은 ResponseEntity
     * @author chadongmin (cdm2883@naver.com)
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

    /**
     * Custom annotation을 이용한 검증시 검증이 실패하면 ConstraintViolationException 이 발생함.
     * ConstraintViolationException는 ValidationException을 상속받고 있어서 ValidationException으로 핸들러를 정의함.
     *
     * @param e ConstraintViolationException
     * @return 에러 응답을 담은 ResponseEntity
     * @author jisang Lee (matrixpower1004@gmail.com)
     */
    @ExceptionHandler
    public ResponseEntity<FailResponse> handleValidationException(
        ValidationException e
    ) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(FailResponse.builder()
            .code(ErrorCode.INVALID_REQUEST_BODY.getCode())
            .message(ErrorCode.INVALID_REQUEST_BODY.getMessage())
            .build()
        );
    }
}
