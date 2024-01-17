package com.backoffice.upjuyanolja.domain.coupon.controller;

import com.backoffice.upjuyanolja.domain.coupon.dto.request.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.exception.InvalidRequestBodyException;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficePrincipalService;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponValidationService;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.backoffice.upjuyanolja.global.validator.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/coupons/backoffice")
@RequiredArgsConstructor
public class CouponBackofficeController {

    private final CouponBackofficeService couponService;
    private final CouponValidationService couponValidationService;
    private final CouponBackofficePrincipalService couponPrincipalService;
    private final SecurityUtil securityUtil;

    @GetMapping("/buy/{accommodationId}")
    public ResponseEntity<SuccessResponse<CouponMakeViewResponse>> responseRoomsByAccommodationId(
        @PathVariable(name = "accommodationId") @ValidId Long accommodationId
    ) {
        //Todo: Id validation 검증 로직 보완
        log.info("GET /api/coupons/backoffice/buy/{accommodationId}");

        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.<CouponMakeViewResponse>builder()
                .message("쿠폰 조회에 성공했습니다.")
                .data(couponService.getRoomsByAccommodation(accommodationId))
                .build()
        );
    }

    @PostMapping("/buy")
    public ResponseEntity<SuccessResponse<Object>> makeCouponsWithRooms(
        @Valid @RequestBody CouponMakeRequest couponMakeRequest
    ) {
        log.info("POST /api/coupons/backoffice/buy");

        if (!couponValidationService.validCouponMakeRequest(
            couponMakeRequest, securityUtil.getCurrentMemberId())
        ) {
            throw new InvalidRequestBodyException();
        }

        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.builder()
                .message("성공적으로 쿠폰이 발급되었습니다.")
                .data(couponPrincipalService.createCoupon(couponMakeRequest.rooms()))
                .build()
        );
    }
}
