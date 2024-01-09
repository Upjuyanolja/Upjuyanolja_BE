package com.backoffice.upjuyanolja.domain.accommodation.controller;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationService;
import com.backoffice.upjuyanolja.global.common.ApiResponse;
import com.backoffice.upjuyanolja.global.common.ApiResponse.SuccessResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<SuccessResponse<AccommodationInfoResponse>> registerAccommodation(
        @Valid @RequestBody AccommodationRegisterRequest accommodationRegisterRequest
    ) {
        return ApiResponse.success(HttpStatus.CREATED,
            SuccessResponse.<AccommodationInfoResponse>builder()
                .message("성공적으로 숙소를 등록했습니다.")
                .data(accommodationService.createAccommodation(securityUtil.getCurrentMemberId(),
                    accommodationRegisterRequest))
                .build()
        );
    }
}
