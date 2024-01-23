package com.backoffice.upjuyanolja.domain.coupon.controller;

import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponAddRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponDeleteRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponModifyRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public ResponseEntity<SuccessResponse<CouponMakeViewResponse>> responseRoomsView(
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
    public ResponseEntity<SuccessResponse<Object>> createCoupons(
        @Valid @RequestBody CouponMakeRequest couponMakeRequest
    ) {
        log.info("POST /api/coupons/backoffice/buy");

        Member currentMember = getCurrentMember();

        couponService.validateAccommodationOwnership(
            couponMakeRequest.accommodationId(), currentMember.getId());
        couponService.createCoupon(couponMakeRequest, currentMember);

        return ApiResponse.success(
            HttpStatus.CREATED,
            SuccessResponse.builder()
                .message("성공적으로 쿠폰이 발급되었습니다.")
                .data(null)
                .build()
        );
    }

    @GetMapping("/manage/{accommodationId}")
    public ResponseEntity<SuccessResponse<CouponManageResponse>> manageCouponView(
        @PathVariable(name = "accommodationId") @Min(1) Long accommodationId
    ) {
        log.info("GET /api/coupons/backoffice/manage/{accommodationId}");

        long currentMemberId = securityUtil.getCurrentMemberId();
        couponService.validateAccommodationOwnership(
            accommodationId, currentMemberId);
        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.<CouponManageResponse>builder()
                .message("쿠폰 조회에 성공하였습니다.")
                .data(couponService.manageCoupon(accommodationId))
                .build()
        );
    }

    @PatchMapping("/manage/buy")
    public ResponseEntity<SuccessResponse<Object>> addonCoupon(
        @Valid @RequestBody CouponAddRequest request
    ) {
        log.info("PATCH /api/coupons/backoffice/manage/buy");

        Long accommodationId = request.accommodationId();
        long currentMemberId = securityUtil.getCurrentMemberId();
        couponService.validateAccommodationOwnership(
            accommodationId, currentMemberId);

        couponService.addonCoupon(request,currentMemberId);

        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.builder()
                .message("쿠폰 추가 구매에 성공하였습니다.")
                .data(null)
                .build()
        );
    }

    @PatchMapping("/manage")
    public ResponseEntity<SuccessResponse<Object>> modifyCoupon(
        @Valid @RequestBody CouponModifyRequest request
    ) {
        log.info("PATCH /api/coupons/backoffice/manage");

        Long accommodationId = request.accommodationId();
        long currentMemberId = securityUtil.getCurrentMemberId();
        couponService.validateAccommodationOwnership(
            accommodationId, currentMemberId);

        couponService.modifyCoupon(request);

        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.builder()
                .message("쿠폰 수정에 성공하였습니다.")
                .data(null)
                .build()
        );
    }

    @DeleteMapping("/manage")
    public ResponseEntity<SuccessResponse<Object>> deleteCoupon(
        @Valid @RequestBody CouponDeleteRequest request
    ) {
        log.info("PATCH /api/coupons/backoffice/manage");

        Long accommodationId = request.accommodationId();
        long currentMemberId = securityUtil.getCurrentMemberId();
        couponService.validateAccommodationOwnership(
            accommodationId, currentMemberId);

        couponService.deleteCoupon(request);

        return ApiResponse.success(
            HttpStatus.OK,
            SuccessResponse.builder()
                .message("쿠폰 삭제에 성공하였습니다.")
                .data(null)
                .build()
        );
    }


    private Member getCurrentMember() {
        Long memberId = securityUtil.getCurrentMemberId();
        return memberGetService.getMemberById(memberId);
    }
}
