package com.backoffice.upjuyanolja.domain.point.entity;

import lombok.Getter;

@Getter
public enum PointCategory {

    CHARGE("충전"),
    REFUND("충전"),
    USE("사용");

    private final String description;

    PointCategory(String description) {
        this.description = description;
    }
}
