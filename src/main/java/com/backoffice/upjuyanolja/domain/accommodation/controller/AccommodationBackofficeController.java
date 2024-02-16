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

/**
 * B2B Backoffice 숙소 관리 API Controller Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/backoffice-api/accommodations")
public class AccommodationBackofficeController {

    /**
     * 숙소 생성, 수정, 삭제 UseCase Interface
     */
    private final AccommodationCommandUseCase accommodationCommandUseCase;

    /**
     * 숙소 조회 UseCase Interface
     */
    private final AccommodationQueryUseCase accommodationQueryUseCase;

    /**
     * Security Utility Class
     * <p>
     * JWT에서 업주 회원 식별자를 추출하기 위해 사용합니다.
     */
    private final SecurityUtil securityUtil;

    /**
     * 숙소 등록 API Controller
     * <p>
     * 업주 회원은 숙소를 등록할 수 있습니다.<br> 숙소 등록을 위해서는 숙소 정보(기본 정보, 옵션, 이미지 1개 이상), 1개 이상의 객실 정보(객실 옵션, 객실이미지
     * 1개 이상)이 필요합니다.
     *
     * @param accommodationRegisterRequest 숙소 등록 요청 DTO
     * @return 등록한 숙소 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @PostMapping
    public ResponseEntity<AccommodationInfoResponse> registerAccommodation(
        @Valid @RequestBody AccommodationRegisterRequest accommodationRegisterRequest
    ) {
        log.info("[POST] /backoffice-api/accommodations");

        AccommodationInfoResponse response = accommodationCommandUseCase.createAccommodation(
            securityUtil.getCurrentMemberId(),
            accommodationRegisterRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 업주 회원 보유 숙소 조회 API Controller
     * <p>
     * 업주 회원은 본인이 등록했던 보유 숙소 목록을 조회할 수 있습니다.
     *
     * @return 업주 회원이 보유한 숙소 정보 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @GetMapping
    public ResponseEntity<AccommodationOwnershipResponse> getAccommodationOwnership() {
        log.info("[GET] /backoffice-api/accommodations");

        AccommodationOwnershipResponse response = accommodationQueryUseCase.getAccommodationOwnership(
            securityUtil.getCurrentMemberId()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
