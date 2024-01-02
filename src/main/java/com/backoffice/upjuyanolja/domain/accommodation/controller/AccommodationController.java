package com.backoffice.upjuyanolja.domain.accommodation.controller;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationService;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @GetMapping
    public ResponseEntity<SuccessResponse<AccommodationPageResponse>> getAccommodations(
        @RequestParam(defaultValue = "ALL", required = false) String category,
        @RequestParam(defaultValue = "false", required = false) boolean onlyHasCoupon,
        @RequestParam(required = false) String keyword,
        @PageableDefault(page = 1, size = 12) Pageable pageable
    ) {
        log.info("GET /api/accommodations");

        AccommodationPageResponse response = accommodationService.findAccommodations(
            category, onlyHasCoupon, keyword, pageable
        );
        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.<AccommodationPageResponse>builder()
                .message("성공적으로 숙소 목록을 조회 했습니다.")
                .data(response)
                .build()
        );
    }

    @GetMapping("/{accommodationId}")
    public ResponseEntity<SuccessResponse<AccommodationDetailResponse>> getAccommodationWithRooms(
        @PathVariable Long accommodationId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    ) {
        log.info("GET /api/accommodations/{accommodationId}");

        AccommodationDetailResponse response = accommodationService.findAccommodationWithRooms(
            accommodationId, startDate, endDate
        );
        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.<AccommodationDetailResponse>builder()
                .message("성공적으로 숙소 상세 목록을 조회 했습니다.")
                .data(response)
                .build()
        );
    }
}
