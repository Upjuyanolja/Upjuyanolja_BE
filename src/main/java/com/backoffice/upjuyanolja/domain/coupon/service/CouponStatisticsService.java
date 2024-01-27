package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponStatisticsResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.RevenueInfo;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.RevenueStatisticsResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import com.backoffice.upjuyanolja.domain.coupon.entity.RevenueStatistics;
import com.backoffice.upjuyanolja.domain.coupon.entity.RevenueTotal;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponStatisticsRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.RevenueStatisticsRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.RevenueTotalRepository;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponStatisticsService {

    private final CouponStatisticsRepository couponStatisticsRepository;
    private final RevenueStatisticsRepository revenueStatisticsRepository;
    private final RevenueTotalRepository revenueTotalRepository;

    // 쿠폰 현황 통계 response
    public CouponStatisticsResponse getCouponStatistics(Long accommodationId) {
        Optional<CouponStatistics> couponStatistics = Optional.ofNullable(couponStatisticsRepository
            .findByAccommodationId(accommodationId)
            .orElseGet(() -> {
                return null;
            }));
        if (couponStatistics.isEmpty()) {
            return null;
        }
        return CouponStatisticsResponse.from(couponStatistics.get());
    }

    // 최근 일주일 일자별 매출 통계 response
    public RevenueStatisticsResponse getRevenueStatistics(
        Long accommodationId, String ownerName
    ) {
        Optional<RevenueStatisticsResponse> response = Optional.ofNullable(
            getRevenueStatisticsResponse(accommodationId, ownerName));
        if (response.isEmpty()) {
            return null;
        }
        return response.get();
    }

    private RevenueStatisticsResponse getRevenueStatisticsResponse(
        Long accommodationId, String ownerName
    ) {
        List<RevenueStatistics> query = revenueStatisticsRepository
            .findByAccommodationId(accommodationId);
        if (query.isEmpty()) {
            return null;
        }

        List<RevenueInfo> infos = new ArrayList<>();
        for (var result : query) {
            infos.add(createRevenueInfo(result));
        }
        Optional<RevenueTotal> revenueTotal = Optional.ofNullable(revenueTotalRepository
            .findByAccommodationId(accommodationId).orElseGet(() -> {
                return null;
            }));
        if (revenueTotal.isEmpty()) {
            return null;
        }

        RevenueTotal result = revenueTotal.get();
        String couponMessage = makeCouponMessage(result, ownerName);
        return RevenueStatisticsResponse.builder()
            .accommodationId(accommodationId)
            .revenue(infos)
            .couponMessage(couponMessage)
            .build();
    }

    private String makeCouponMessage(RevenueTotal revenueTotal, String ownerName) {
        int growth = (int) revenueTotal.getGrowthRate();
        if (growth <= 10) {
            return new String();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ownerName).append("님, 쿠폰 발급 후 매출이 ").append(growth)
            .append("% 늘어났어요!");
        return sb.toString();
    }

    private RevenueStatisticsResponse createRevenueResponse(
        Long accommodationId, List<RevenueStatistics> query) {
        List<RevenueInfo> infos = query.stream()
            .map(this::createRevenueInfo).collect(Collectors.toList());
        return RevenueStatisticsResponse.builder()
            .accommodationId(accommodationId)
            .revenue(infos)
            .build();
    }

    private RevenueInfo createRevenueInfo(RevenueStatistics revenueStatistics) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        return RevenueInfo.builder()
            .couponRevenue(revenueStatistics.getCouponRevenue())
            .normalRevenue(revenueStatistics.getRegularRevenue())
            .revenueDate(revenueStatistics.getRevenueDate().format(formatter))
            .build();
    }
}
