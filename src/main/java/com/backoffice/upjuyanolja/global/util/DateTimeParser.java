package com.backoffice.upjuyanolja.global.util;

import com.backoffice.upjuyanolja.domain.room.exception.InvalidTimeFormatException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class DateTimeParser {

    public static LocalTime timeParser(String dateTimeString) {
        try {
            String pattern = "HH:mm";
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(
                pattern);

            return LocalTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidTimeFormatException();
        }
    }
}
