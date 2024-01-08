package com.backoffice.upjuyanolja.domain.accommodation.entity;

import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongAccommodationTypeException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum AccommodationType {

    // 호텔/리조트
    TOURIST_HOTEL("관광호텔", "B02010100", Category.HOTEL_RESORT),
    CONDOMINIUM("콘도미니엄", "B02010500", Category.HOTEL_RESORT),
    RESIDENCE("레지던스", "B02011300", Category.HOTEL_RESORT),

    // 모텔
    MOTEL("모텔", "B02010900", Category.MOTEL),

    // 펜션/풀빌라
    PENSION("펜션", "B02010700", Category.MOTEL),
    POOL_VILLA("풀빌라", "B02012000", Category.MOTEL),

    // 게스트하우스
    GUEST_HOUSE("게스트하우스", "B02011100", Category.GUEST_HOUSE),
    HANOK("한옥", "B02011600", Category.GUEST_HOUSE);

    private final String name;
    private final String code;
    private final Category category;

    AccommodationType(String name, String code, Category category) {
        this.name = name;
        this.code = code;
        this.category = category;
    }

    public static AccommodationType getByName(String name) {
        return Arrays.stream(AccommodationType.values())
            .filter(val -> val.getName().equals(name))
            .findFirst()
            .orElseThrow(WrongAccommodationTypeException::new);
    }

    public static AccommodationType getByCode(String code) {
        return Arrays.stream(AccommodationType.values())
            .filter(val -> val.getCode().equals(code))
            .findFirst()
            .orElseThrow(WrongAccommodationTypeException::new);
    }
}
