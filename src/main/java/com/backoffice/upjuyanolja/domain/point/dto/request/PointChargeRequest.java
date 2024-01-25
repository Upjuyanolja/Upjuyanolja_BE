package com.backoffice.upjuyanolja.domain.point.dto.request;

import io.jsonwebtoken.lang.Assert;

public record PointChargeRequest(
    String paymentKey,
    String orderId,
    long amount
) {
    public PointChargeRequest{
        Assert.hasText(paymentKey, "결제 식별 키를 입력하세요.");
        Assert.hasText(orderId, "주문 번호를 입력하세요.");
        Assert.isTrue(amount >= 10000, "포인트 결제 가격을 10,000 이상으로 입력하세요.");
    }
}
