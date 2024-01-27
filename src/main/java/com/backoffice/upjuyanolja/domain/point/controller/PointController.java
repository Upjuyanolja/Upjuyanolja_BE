package com.backoffice.upjuyanolja.domain.point.controller;

import com.backoffice.upjuyanolja.domain.point.dto.request.PointChargeRequest;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargePageResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointSummaryResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointTotalBalanceResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointTotalPageResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointUsagePageResponse;
import com.backoffice.upjuyanolja.domain.point.service.PointService;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import jakarta.validation.Valid;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/points")
@RequiredArgsConstructor
@Validated
public class PointController {

    private final PointService pointService;
    private final SecurityUtil securityUtil;

    @GetMapping("/summary")
    public ResponseEntity<PointSummaryResponse> getPointSummary(
        @RequestParam(required = true)
        @DateTimeFormat(pattern = "yyyy-MM")
        YearMonth rangeDate
    ) {
        log.info("GET /api/points/summary");

        PointSummaryResponse response = pointService.getPointSummaryResponse(
            securityUtil.getCurrentMemberId(), rangeDate
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/totalBalance")
    public ResponseEntity<PointTotalBalanceResponse> getPointTotalBalance() {
        log.info("GET /api/points/totalBalance");

        PointTotalBalanceResponse response = pointService.getPointTotalBalanceResponse(
            securityUtil.getCurrentMemberId()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/charges")
    public ResponseEntity<PointChargePageResponse> getChargePoints(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        log.info("Get /api/points/charges");

        PointChargePageResponse response = pointService.getPointChargePageResponse(
            securityUtil.getCurrentMemberId(), pageable
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/charges/{chargeId}")
    public ResponseEntity<PointChargeResponse> getDetailChargePoints(
        @PathVariable Long chargeId
    ) {
        log.info("Get /api/points/charges/{chargeId}");

        PointChargeResponse response = pointService.getDetailChargePointResponse(chargeId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/usages")
    public ResponseEntity<PointUsagePageResponse> getUsagePoints(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        log.info("Get /api/points/usages");

        PointUsagePageResponse response = pointService.getPointUsagePageResponse(
            securityUtil.getCurrentMemberId(), pageable
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/total")
    public ResponseEntity<PointTotalPageResponse> getTotalPoints(
        @PageableDefault(page = 0, size = 4) Pageable pageable
    ) {
        log.info("Get /api/points/total");

        PointTotalPageResponse response = pointService.getTotalPointPageResponse(
            securityUtil.getCurrentMemberId(), pageable
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/charges")
    public ResponseEntity<PointChargeResponse> chargePoint(
        @Valid @RequestBody PointChargeRequest request
    ) {
        log.info("Post /api/points/charges");

        PointChargeResponse response = pointService.chargePoint(
            securityUtil.getCurrentMemberId(), request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/charges/{chargeId}")
    public ResponseEntity<Void> refundPoint(
        @PathVariable Long chargeId
    ) {
        log.info("Get /api/points/charges/{chargeId}");

        pointService.refundPoint(chargeId);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
