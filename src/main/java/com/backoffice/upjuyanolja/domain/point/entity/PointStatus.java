package com.backoffice.upjuyanolja.domain.point.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointStatus {

    CONFIRMED("구매 확정"),
    PAID("결제 완료"),
    CANCELED("취소 완료");

    private final String description;

}
