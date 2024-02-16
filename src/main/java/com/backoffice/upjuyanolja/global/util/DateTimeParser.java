package com.backoffice.upjuyanolja.global.util;

import com.backoffice.upjuyanolja.domain.room.exception.InvalidTimeFormatException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * 일시 변환 Utility Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public class DateTimeParser {

    /**
     * 시간 변환 메서드
     *
     * @param timeString 시간 문자열
     * @return LocalTime으로 변환한 시간
     * @throws InvalidTimeFormatException 날짜 형식이 맞지 않는 경우 에러 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static LocalTime timeParser(String timeString) {
        try {
            String pattern = "HH:mm";
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(
                pattern);

            return LocalTime.parse(timeString, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeFormatException();
        }
    }
}
