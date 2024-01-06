package com.backoffice.upjuyanolja.global.common;

import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;
import java.util.Arrays;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {

    public static <T> ResponseEntity<SuccessResponse<T>> success(
        HttpStatus status,
        SuccessResponse<T> body
    ) {
        return ResponseEntity.status(status).body(body);
    }

    public static ResponseEntity<FailResponse> error(FailResponse responseDto) {
        return ResponseEntity
            .status(FailResponse.getHttpStatusByCode(responseDto.code))
            .body(responseDto);
    }

    @Builder
    public record SuccessResponse<T>(String message, T data) {

    }

    @Builder
    public record FailResponse(int code, String message) {

        public static HttpStatus getHttpStatusByCode(int code) {
            return Arrays.stream(ErrorCode.values())
                .filter(val -> val.getCode() == code)
                .findFirst()
                .orElseThrow(WrongCategoryException::new)
                .getHttpStatus();
        }
    }
}
