package com.backoffice.upjuyanolja.domain.coupon.entity;

import lombok.Getter;

@Getter
public enum Status {

    ISSUING("발급 중"),
    SUSPENDED("발급 중지"),
    EXHAUSTED("소진");

    private final String description;

    Status(String description) {
        this.description = description;
    }

}
