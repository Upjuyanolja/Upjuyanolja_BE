package com.backoffice.upjuyanolja.domain.point.entity;

import lombok.Getter;

@Getter
public enum PointType {

    CHARGE("충전"),
    USE("사용");

    private final String description;

    PointType(String description) {
        this.description = description;
    }
}
