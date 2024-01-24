package com.backoffice.upjuyanolja.domain.point.dto.response;

import lombok.Builder;

@Builder
public record TossChargeResponse(
    String paymentKey,
    String orderId,
    long amount,
    String approvedAt,
    String tradeAt,
    String orderName
) {

}
