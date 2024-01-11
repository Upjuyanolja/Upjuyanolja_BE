package com.backoffice.upjuyanolja.domain.accommodation.controller;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationCommandUseCase;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<SuccessResponse<AccommodationInfoResponse>> registerAccommodation(
        @Valid @RequestBody AccommodationRegisterRequest accommodationRegisterRequest
    ) {
        log.info("POST /api/accommodations");

        return ApiResponse.success(HttpStatus.CREATED,
            SuccessResponse.<AccommodationInfoResponse>builder()
                .message("성공적으로 숙소를 등록했습니다.")
                .data(accommodationCommandUseCase.createAccommodation
                    (securityUtil.getCurrentMemberId(), accommodationRegisterRequest)
                )
                .build()
        );
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<AccommodationPageResponse>> getAccommodations(
        @RequestParam(defaultValue = "ALL", required = false) String category,
        @RequestParam(defaultValue = "ALL", required = false) String type,
        @RequestParam(defaultValue = "false", required = false) boolean onlyHasCoupon,
        @Valid @RequestParam(required = false)
        @Length(min = 1, max = 30, message = "검색어는 1 글자 ~ 30 글자 사이 여야 합니다")
        String keyword,
        @PageableDefault(page = 0, size = 12) Pageable pageable
    ) {
        log.info("GET /api/accommodations");

        AccommodationPageResponse response = accommodationCommandUseCase.findAccommodations(
            category, type, onlyHasCoupon, keyword, pageable
        );
        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.<AccommodationPageResponse>builder()
                .message("숙소 목록 조회에 성공 했습니다.")
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

        AccommodationDetailResponse response = accommodationCommandUseCase.findAccommodationWithRooms(
            accommodationId, startDate, endDate
        );
        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.<AccommodationDetailResponse>builder()
                .message("숙소 상세 목록 조회에 성공 했습니다.")
                .data(response)
                .build()
        );
    }

    @GetMapping("/backoffice")
    public ResponseEntity<SuccessResponse<AccommodationOwnershipResponse>> getAccommodationOwnership() {
        log.info("GET /api/accommodations/backoffice");

        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<AccommodationOwnershipResponse>builder()
                .message("성공적으로 보유 숙소 목록을 조회했습니다.")
                .data(accommodationCommandUseCase
                    .getAccommodationOwnership(securityUtil.getCurrentMemberId()))
                .build()
        );
    }
}
