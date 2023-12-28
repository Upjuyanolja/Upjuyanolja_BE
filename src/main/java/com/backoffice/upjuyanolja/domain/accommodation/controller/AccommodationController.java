package com.backoffice.upjuyanolja.domain.accommodation.controller;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationService;
import com.backoffice.upjuyanolja.global.common.response.ResponseBody;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseBody<AccommodationPageResponse> getAccommodations(
        @RequestParam(defaultValue = "ALL", required = false) Category category,
        @RequestParam(defaultValue = "false") Boolean hasCoupon,
        @RequestParam(defaultValue = "") String keyword,
        @PageableDefault(page = 1, size = 12) Pageable pageable
    ) {
        AccommodationPageResponse accommodations =
            accommodationService.findAccommodationsWithSoldOutCheck(
                category, pageable
            );
        return ResponseBody.ok(accommodations);
    }

    @GetMapping("/{accommodationId}")
    public ResponseBody<AccommodationDetailResponse> getAccommodationWithRooms(
        @PathVariable Long accommodationId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    ) {
        return ResponseBody.ok(accommodationService.findAccommodationWithRooms(
            accommodationId, startDate, endDate)
        );
    }
}
