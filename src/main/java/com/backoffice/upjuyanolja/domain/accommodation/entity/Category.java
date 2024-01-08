package com.backoffice.upjuyanolja.domain.accommodation.entity;

import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Category {

    HOTEL_RESORT("호텔/리조트"),
    MOTEL("모텔"),
    PENSION_POOL_VILLA("펜션/풀빌라"),
    GUEST_HOUSE("게스트하우스");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public static Category getByName(String name) {
        return Arrays.stream(Category.values())
            .filter(val -> val.getName().equals(name))
            .findFirst()
            .orElseThrow(WrongCategoryException::new);
    }
}
