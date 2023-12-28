package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record AccommodationPageResponse(
    Integer pageNum,
    Integer pageSize,
    Integer totalPages,
    Long totalElements,
    Boolean isLast,
    List<AccommodationSummaryResponse> accommodations

) {

    public static AccommodationPageResponse from(Page<AccommodationSummaryResponse> dto) {
        return AccommodationPageResponse.builder()
            .pageNum(dto.getNumber() + 1)
            .pageSize(dto.getSize())
            .totalPages(dto.getTotalPages())
            .totalElements(dto.getTotalElements())
            .isLast(dto.isLast())
            .accommodations(dto.getContent())
            .build();
    }
}
