package com.backoffice.upjuyanolja.domain.accommodation.controller;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationCommandUseCase;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice-api/accommodations")
@RequiredArgsConstructor
public class AccommodationBackofficeController {

    private final AccommodationCommandUseCase accommodationCommandUseCase;
    private final AccommodationQueryUseCase accommodationQueryUseCase;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<AccommodationInfoResponse> registerAccommodation(
        @Valid @RequestBody AccommodationRegisterRequest accommodationRegisterRequest
    ) {
        log.info("POST /backoffice-api/accommodations");

        AccommodationInfoResponse response = accommodationCommandUseCase
            .createAccommodation(securityUtil.getCurrentMemberId(), accommodationRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<AccommodationOwnershipResponse> getAccommodationOwnership() {
        log.info("GET /backoffice-api/accommodations");

        AccommodationOwnershipResponse response = accommodationQueryUseCase
            .getAccommodationOwnership(securityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
