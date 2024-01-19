package com.backoffice.upjuyanolja.domain.coupon.controller;

import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class CouponBackofficeController {

    private final CouponBackofficeService couponService;
    private final SecurityUtil securityUtil;
    private final MemberGetService memberGetService;

    @GetMapping("/buy/{accommodationId}")
    public ResponseEntity<SuccessResponse<CouponMakeViewResponse>> responseRoomsByAccommodationId(
        @PathVariable(name = "accommodationId") @Min(1) Long accommodationId
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
    public ResponseEntity<SuccessResponse<Object>> createCouponsByRooms(
        @Valid @RequestBody CouponMakeRequest couponMakeRequest
    ) {
        log.info("POST /api/coupons/backoffice/buy");

        Member currentMember = getCurrentMember();

        couponService.validateCouponRequest(couponMakeRequest, currentMember.getId());
        couponService.createCoupon(couponMakeRequest, currentMember);

        return ApiResponse.success(
            HttpStatus.CREATED,
            SuccessResponse.builder()
                .message("성공적으로 쿠폰이 발급되었습니다.")
                .data(null)
                .build()
        );
    }

    private Member getCurrentMember() {
        Long memberId = securityUtil.getCurrentMemberId();
        return memberGetService.getMemberById(memberId);
    }

}
