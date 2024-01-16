package com.backoffice.upjuyanolja.domain.openapi.dto;

import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum AccommodationType {

    // 호텔/리조트
    TOURIST_HOTEL("관광호텔", "B02010100"),
    CONDOMINIUM("콘도", "B02010500"),
    RESIDENCE("레지던스", "B02011300"),

    // 모텔
    MOTEL("모텔", "B02010900"),

    // 펜션/풀빌라
    PENSION_POOL_VILLA("펜션/풀빌라", "B02010700"),

    // 게스트 하우스
    GUEST_HOUSE("게스트하우스", "B02011100"),
    HANOK("한옥", "B02011600");

    private final String name;
    private final String code;

    AccommodationType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static AccommodationType getByCode(String code) {
        return Arrays.stream(AccommodationType.values())
            .filter(val -> val.getCode().equals(code))
            .findFirst()
            .orElseThrow(WrongCategoryException::new);
    }
}
