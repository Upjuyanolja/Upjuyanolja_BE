package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record AccommodationPageResponse(
    int pageNum,
    int pageSize,
    int totalPages,
    long totalElements,
    boolean isLast,
    List<AccommodationSummaryResponse> accommodations

) {

    public static AccommodationPageResponse of(Page<AccommodationSummaryResponse> responsePage) {
        return AccommodationPageResponse.builder()
            .pageNum(responsePage.getNumber() + 1)
            .pageSize(responsePage.getSize())
            .totalPages(responsePage.getTotalPages())
            .totalElements(responsePage.getTotalElements())
            .isLast(responsePage.isLast())
            .accommodations(responsePage.getContent())
            .build();
    }
}
