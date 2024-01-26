package com.backoffice.upjuyanolja.domain.accommodation.controller;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationCommandUseCase;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
@Validated
public class AccommodationController {

    private final AccommodationCommandUseCase accommodationCommandUseCase;

    @GetMapping
    public ResponseEntity<AccommodationPageResponse> getAccommodations(
        @RequestParam(defaultValue = "ALL", required = false) String category,
        @RequestParam(defaultValue = "false", required = false) boolean onlyHasCoupon,
        @Valid @RequestParam(required = false)
        @Length(min = 1, max = 30, message = "검색어는 1 글자 ~ 30 글자 사이 여야 합니다")
        String keyword,
        @PageableDefault(page = 0, size = 12) Pageable pageable
    ) {
        log.info("GET /api/accommodations");

        AccommodationPageResponse response = accommodationCommandUseCase
            .findAccommodations(category, onlyHasCoupon, keyword, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{accommodationId}")
    public ResponseEntity<AccommodationDetailResponse> getAccommodationWithRooms(
        @PathVariable Long accommodationId,
        @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}", required = false)
        @DateTimeFormat(iso = ISO.DATE)
        LocalDate startDate,
        @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}", required = false)
        @DateTimeFormat(iso = ISO.DATE)
        LocalDate endDate
    ) {
        log.info("GET /api/accommodations/{accommodationId}");

        AccommodationDetailResponse response = accommodationCommandUseCase
            .findAccommodationWithRooms(accommodationId, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
