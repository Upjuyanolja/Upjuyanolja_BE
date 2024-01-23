package com.backoffice.upjuyanolja.domain.coupon.controller;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponAccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/{accommodationId}")
    public ResponseEntity<List<CouponAccommodationResponse>> getCouponInAccommodation(
        @PathVariable Long accommodationId
    ) {
        log.info("GET /api/coupons/{accommodationId}");

        List<CouponAccommodationResponse> response =
            couponService.findCouponResponseInAccommodation(accommodationId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
