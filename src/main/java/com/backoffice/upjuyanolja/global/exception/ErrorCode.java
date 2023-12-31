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
    AUTHORIZE_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, 1006, "인증 정보를 찾을 수 없습니다"),

    // Accommodation
    ACCOMMODATION_NOT_FOUND(HttpStatus.NOT_FOUND, 2000, "숙소 정보를 찾을 수 없습니다."),
    RESERVATION_NOT_POSSIBLE(HttpStatus.BAD_REQUEST, 2001, "예약이 불가능한 방입니다."),
    WRONG_ACCOMMODATION_TYPE(HttpStatus.BAD_REQUEST, 2002, "알맞지 않은 숙소 종류입니다."),
    WRONG_CATEGORY(HttpStatus.BAD_REQUEST, 2003, "알맞지 않은 카테고리입니다."),

    // Room
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, 3000, "객실 정보를 찾을 수 없습니다."),

    // Reservation
    RESERVATION_FAILED(HttpStatus.BAD_REQUEST, 4000, "예약할 수 없습니다."),
    INVALID_COUPON(HttpStatus.BAD_REQUEST, 4001, "쿠폰이 유효하지 않습니다."),
    PREEMPTION_EXPIRED(HttpStatus.BAD_REQUEST, 4002, "선점 기한이 만료되어 예약할 수 없습니다."),
    RESERVATION_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, 4003, "예약 숙소 정보를 찾을 수 없습니다."),
    INVALID_VISITOR_NAME(HttpStatus.BAD_REQUEST, 4004, "유효하지 않는 방문자 전화번호 입니다."),
    INVALID_VISITOR_PHONE_NUMBER(HttpStatus.BAD_REQUEST, 4005, "유효하지 않는 방문자 전화번호입니다."),

    // Coupon
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, 5000, "쿠폰 정보를 찾을 수 없습니다."),
    WRONG_COUPON_INFO(HttpStatus.BAD_REQUEST, 5001, "쿠폰 정보가 올바르지 않습니다."),

    // OpenAPI
    OPEN_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 8000, "오픈 API를 이용하는 중 에러가 발생했습니다."),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, 8001, "오픈 API에서 가져온 숙소 데이터에서 필요한 정보가 없어, 다음 숙소를 조회합니다."),

    // Common
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9000, "데이터베이스 오류가 발생했습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, 9001, "유효하지 않은 요청 바디입니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9002, "서버에 알 수 없는 에러가 발생했습니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, 9003, "유효하지 않는 날짜입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
