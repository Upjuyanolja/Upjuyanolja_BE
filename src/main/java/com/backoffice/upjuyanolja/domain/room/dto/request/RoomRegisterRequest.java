package com.backoffice.upjuyanolja.domain.room.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomRegisterRequest(
    @NotBlank(message = "객실 이름을 입력하세요.")
    @Size(min = 1, max = 30, message = "객실 이름을 1~30자로 입력하세요.")
    String name,

    @NotNull(message = "객실 가격을 입력하세요.")
    @Min(value = 10000, message = "객실 가격을 10,000원 이상으로 입력하세요.")
    @Max(value = 1000000, message = "객실 가격을 1,000,000원 이하로 입력하세요.")
    int price,

    @NotNull(message = "객실 기본 인원을 입력하세요.")
    @Min(value = 1, message = "객실 기본 인원을 1이상으로 입력하세요.")
    @Max(value = 15, message = "객실 기본 인원을 15이하로 입력하세요.")
    int defaultCapacity,

    @NotNull(message = "객실 최대 인원을 입력하세요.")
    @Min(value = 1, message = "객실 최대 인원을 1이상으로 입력하세요.")
    @Max(value = 15, message = "객실 최대 인원을 15이하로 입력하세요.")
    int maxCapacity,

    @NotBlank(message = "체크인 시간을 입력하세요.")
    @Pattern(regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d$", message = "체크인 시간을 HH:mm 형식의 24시간 표기법으로 입력하세요.")
    String checkInTime,

    @NotBlank(message = "체크아웃 시간을 입력하세요.")
    @Pattern(regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d$", message = "체크아웃 시간을 HH:mm 형식의 24시간 표기법으로 입력하세요.")
    String checkOutTime,

    @NotNull(message = "객실 수량을 입력하세요.")
    int amount,

    @NotNull(message = "객실 이미지를 한 개 이상 입력하세요.")
    List<RoomImageRequest> images,

    @NotNull(message = "객실 옵션을 입력하세요.")
    RoomOptionRequest option
) {

}
