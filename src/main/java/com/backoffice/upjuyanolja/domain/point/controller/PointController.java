package com.backoffice.upjuyanolja.domain.point.controller;

import com.backoffice.upjuyanolja.domain.point.dto.response.PointSummaryResponse;
import com.backoffice.upjuyanolja.domain.point.service.PointService;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final SecurityUtil securityUtil;

    @GetMapping("/summary")
    public ResponseEntity<PointSummaryResponse> getSummary(
        @RequestParam(required = true)
        @DateTimeFormat(pattern = "yyyy-MM")
        YearMonth rangeDate
    ) {
        log.info("GET /api/points/summary");

        PointSummaryResponse response = pointService.getSummary(
            securityUtil.getCurrentOwnerId(), rangeDate
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
