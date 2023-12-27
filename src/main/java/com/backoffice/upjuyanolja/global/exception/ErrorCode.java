package com.backoffice.upjuyanolja.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Member
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, 1000, "중복된 이메일입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 1001, "유효하지 않는 리프레시 토큰입니다."),
    LOGGED_OUT(HttpStatus.UNAUTHORIZED, 1002, "로그아웃한 회원입니다."),
    INCORRECT_EMAIL(HttpStatus.BAD_REQUEST, 1003, "이메일이 일치하지 않습니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, 1004, "비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 1005, "회원 정보를 찾을 수 없습니다."),

    // Accommodation
    ACCOMMODATION_NOT_FOUND(HttpStatus.NOT_FOUND, 2000, "숙소 정보를 찾을 수 없습니다."),
    RESERVATION_NOT_POSSIBLE(HttpStatus.BAD_REQUEST, 2001, "예약이 불가능한 방입니다."),

    // Room
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, 2002, "객실 정보를 찾을 수 없습니다."),

    // Reservation
    RESERVATION_FAILED(HttpStatus.BAD_REQUEST, 3000, "예약할 수 없습니다."),
    INVALID_COUPON(HttpStatus.BAD_REQUEST, 3001, "쿠폰이 유효하지 않습니다."),
    PREEMPTION_EXPIRED(HttpStatus.BAD_REQUEST, 3002, "선점 기한이 만료되어 예약할 수 없습니다."),
    RESERVATION_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, 3003, "예약 숙소 정보를 찾을 수 없습니다."),
    INVALID_VISITOR_NAME(HttpStatus.BAD_REQUEST, 3004, "유효하지 않는 방문자 전화번호 입니다."),
    INVALID_VISITOR_PHONE_NUMBER(HttpStatus.BAD_REQUEST, 3005, "유효하지 않는 방문자 전화번호입니다."),

    // Coupon
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, 4000, "쿠폰 정보를 찾을 수 없습니다."),

    // OpenAPI
    OPEN_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "오픈 API를 이용하는 중 에바가 발생했습니다."),

    // Common
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6000, "데이터베이스 오류가 발생했습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, 6001, "유효하지 않은 요청 바디입니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6002, "서버에 알 수 없는 에러가 발생했습니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, 6003, "유효하지 않는 날짜입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
