package com.backoffice.upjuyanolja.domain.point.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointStatus {

    CANCELED("취소 완료", "충전", "취소"),
    PAID("결제 완료", "충전", "포인트"),
    REMAINED("잔액 존재", "충전", "포인트"),
    USED("구매 확정", "사용", "포인트");

    private final String status;
    private final String category;
    private final String type;

}
