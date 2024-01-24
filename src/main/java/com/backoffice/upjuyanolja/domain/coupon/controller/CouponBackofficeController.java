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
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/buy/{accommodationId}")
    public ResponseEntity<CouponMakeViewResponse> responseRoomsView(
        @PathVariable(name = "accommodationId") @Min(1) Long accommodationId
    ) {
        //Todo: Id validation 검증 로직 보완
        log.info("GET /api/coupons/backoffice/buy/{accommodationId}");
        Long currentMemberId = securityUtil.getCurrentMemberId();
        couponService.validateAccommodationRequest(
            accommodationId, currentMemberId);

        CouponMakeViewResponse response = couponService.getRoomsByAccommodation(accommodationId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/buy")
    public ResponseEntity<Object> createCoupons(
        @Valid @RequestBody CouponMakeRequest couponMakeRequest
    ) {
        log.info("POST /api/coupons/backoffice/buy");

        Member currentMember = getCurrentMember();

        couponService.validateAccommodationRequest(
            couponMakeRequest.accommodationId(), currentMember.getId());
        couponService.createCoupon(couponMakeRequest, currentMember);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/manage/{accommodationId}")
    public ResponseEntity<CouponManageResponse> manageCouponView(
        @PathVariable(name = "accommodationId") @Min(1) Long accommodationId
    ) {
        log.info("GET /api/coupons/backoffice/manage/{accommodationId}");

        long currentMemberId = securityUtil.getCurrentMemberId();
        couponService.validateAccommodationRequest(
            accommodationId, currentMemberId);
        CouponManageResponse response = couponService.manageCoupon(accommodationId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/manage/buy")
    public ResponseEntity<Object> addonCoupon(
        @Valid @RequestBody CouponAddRequest request
    ) {
        log.info("PATCH /api/coupons/backoffice/manage/buy");

        Long accommodationId = request.accommodationId();
        long currentMemberId = securityUtil.getCurrentMemberId();
        couponService.validateAccommodationRequest(
            accommodationId, currentMemberId);

        couponService.addonCoupon(request, currentMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/manage")
    public ResponseEntity<Object> modifyCoupon(
        @Valid @RequestBody CouponModifyRequest request
    ) {
        log.info("PATCH /api/coupons/backoffice/manage");

        Long accommodationId = request.accommodationId();
        long currentMemberId = securityUtil.getCurrentMemberId();

        couponService.validateAccommodationRequest(
            accommodationId, currentMemberId);

        couponService.modifyCoupon(request);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/manage")
    public ResponseEntity<Object> deleteCoupon(
        @Valid @RequestBody CouponDeleteRequest request
    ) {
        log.info("PATCH /api/coupons/backoffice/manage");

        Long accommodationId = request.accommodationId();
        long currentMemberId = securityUtil.getCurrentMemberId();

        couponService.validateAccommodationRequest(
            accommodationId, currentMemberId);

        couponService.deleteCoupon(request);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    private Member getCurrentMember() {
        Long memberId = securityUtil.getCurrentMemberId();
        return memberGetService.getMemberById(memberId);
    }
}
