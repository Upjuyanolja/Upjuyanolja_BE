package com.backoffice.upjuyanolja.global.common.response;

import com.backoffice.upjuyanolja.global.exception.ErrorCode;
import java.util.Arrays;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * API 응답 시 일관성 있는 응답을 할 수 있도록 돕는 Utility Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class ApiResponse {

    /**
     * API 에러 응답 시 사용할 정적 메서드
     * <p>
     * 애러 코드와 에러 메시지를 포함하는 FailResponse 객체를 ResponseEntity에 적절히 담아 응답할 수 있도록 돕습니다.
     *
     * @param failResponse 실패 응답 DTO
     * @return 실패 응답 DTO를 담은 ResponseEntity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static ResponseEntity<FailResponse> error(FailResponse failResponse) {
        return ResponseEntity
            .status(FailResponse.getHttpStatusByCode(failResponse.code))
            .body(failResponse);
    }

    /**
     * 실패 응답 DTO Record
     *
     * @param code    에러 코드
     * @param message 에러 메시지
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    public record FailResponse(int code, String message) {

        /**
         * Custom ErrorCode에서 HttpStatus를 추출하는 정적 메서드
         *
         * @param code 에러 코드
         * @return 에러 코드에 해당 하는 HttpStatus
         * @author JeongUijeong (jeong275117@gmail.com)
         */
        public static HttpStatus getHttpStatusByCode(int code) {
            return Arrays.stream(ErrorCode.values())
                .filter(val -> val.getCode() == code)
                .findFirst()
                .orElse(ErrorCode.SERVER_ERROR)
                .getHttpStatus();
        }
    }
}
