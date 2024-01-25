package com.backoffice.upjuyanolja.domain.point.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointType {

    POINT("포인트"),
    REFUND("취소");

    private final String description;


}
