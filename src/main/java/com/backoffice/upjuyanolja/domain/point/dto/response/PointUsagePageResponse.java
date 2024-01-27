package com.backoffice.upjuyanolja.domain.point.dto.response;

import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PointUsagePageResponse(
    int pageNum,
    int pageSize,
    int totalPages,
    long totalElements,
    boolean isLast,
    List<PointUsageDetailResponse> histories

) {

    public static PointUsagePageResponse of(Page<PointUsageDetailResponse> responsePage) {
        return PointUsagePageResponse.builder()
            .pageNum(responsePage.getNumber() + 1)
            .pageSize(responsePage.getSize())
            .totalPages(responsePage.getTotalPages())
            .totalElements(responsePage.getTotalElements())
            .isLast(responsePage.isLast())
            .histories(responsePage.getContent())
            .build();
    }
}
