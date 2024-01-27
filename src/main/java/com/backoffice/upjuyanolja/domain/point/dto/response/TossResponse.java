package com.backoffice.upjuyanolja.domain.point.dto.response;

import lombok.Builder;

@Builder
public record TossResponse(
    String paymentKey,
    String orderId,
    long totalAmount,
    String requestedAt,
    String orderName,
    String method
) {

}
