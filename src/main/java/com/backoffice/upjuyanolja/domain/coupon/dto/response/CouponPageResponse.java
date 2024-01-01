package com.backoffice.upjuyanolja.domain.coupon.dto.response;

import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record CouponPageResponse(
    int pageNum,
    int pageSize,
    int totalPages,
    long totalElements,
    boolean isLast,
    List<CouponDetailResponse> coupons

) {

    public static CouponPageResponse from(Page<CouponDetailResponse> responsePage) {
        return CouponPageResponse.builder()
            .pageNum(responsePage.getNumber() + 1)
            .pageSize(responsePage.getSize())
            .totalPages(responsePage.getTotalPages())
            .totalElements(responsePage.getTotalElements())
            .isLast(responsePage.isLast())
            .coupons(responsePage.getContent())
            .build();
    }

}
