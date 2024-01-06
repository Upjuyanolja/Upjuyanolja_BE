package com.backoffice.upjuyanolja.global.common.response;

import com.backoffice.upjuyanolja.global.exception.ErrorCode;
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
        return ResponseEntity.status(responseDto.code.getHttpStatus()).body(responseDto);
    }

    @Builder
    public record SuccessResponse<T>(String message, T data) {

    }

    @Builder
    public record FailResponse(ErrorCode code, String message) {

    }
}
