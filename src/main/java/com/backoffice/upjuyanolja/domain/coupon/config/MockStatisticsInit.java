package com.backoffice.upjuyanolja.domain.coupon.config;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.statistics.CouponStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.dto.statistics.RevenueStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import com.backoffice.upjuyanolja.domain.coupon.entity.RevenueStatistics;
import com.backoffice.upjuyanolja.domain.coupon.entity.RevenueTotal;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponStatisticsRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.RevenueStatisticsRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.RevenueTotalRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Configuration
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class MockStatisticsInit {

    private final CouponStatisticsRepository couponStatisticsRepository;
    private final RevenueStatisticsRepository revenueStatisticsRepository;
    private final RevenueTotalRepository revenueTotalRepository;
    private final AccommodationRepository accommodationRepository;

    // Todo: 테스트용. 발표 끝나면 파일 삭제
    @Bean
    ApplicationRunner init() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                if (!couponStatisticsRepository.existsById(1L)) {
                    makeCouponStatistics();
                }
                if (!revenueStatisticsRepository.existsById(1L)) {
                    createRevenueStatistics();
                }
            }
        };
    }

    public void makeCouponStatistics() {
        List<CouponStatisticsInterface> result = couponStatisticsRepository.createStatistics();
        List<CouponStatistics> statisticsList = new ArrayList<>();
        for (CouponStatisticsInterface statistics : result) {
            statisticsList.add(createCouponStatistics(statistics));
        }
        couponStatisticsRepository.saveAll(statisticsList);
        log.info("쿠폰 통계 생성 성공. 총 {}건.", statisticsList.size());
    }

    private CouponStatistics createCouponStatistics(CouponStatisticsInterface statistics) {
        Accommodation accommodation = accommodationRepository.findById(statistics.getId())
            .orElseThrow(AccommodationNotFoundException::new);
        return CouponStatistics.builder()
            .accommodation(accommodation)
            .stock(statistics.getStock())
            .total(statistics.getTotal())
            .used(statistics.getUsed())
            .build();
    }

    public void createRevenueStatistics() {
        // 1. 최근 일주일 일자별 통계를 구한다.
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate endDate = now.minusDays(1);
        LocalDate startDate = now.minusWeeks(1).minusDays(1);

        List<RevenueStatisticsInterface> results = revenueStatisticsRepository
            .createRevenueStatistics(startDate, endDate);

        List<RevenueStatistics> revenueStatistics = new ArrayList<>();

        // 2. for-loop 도는 동안 숙소별로 일주일간의 매출 합계를 구한다.
        Map<Accommodation, long[]> revenueTotal = new HashMap<>();
        for (RevenueStatisticsInterface result : results) {
            Accommodation accommodation = accommodationRepository.findById(result.getId())
                .orElseThrow(AccommodationNotFoundException::new);
            revenueTotal.putIfAbsent(accommodation, new long[2]);
            long[] sum = revenueTotal.get(accommodation);
            sum[0] += result.getCouponRevenue();
            sum[1] += result.getRegularRevenue();
            revenueStatistics.add(createRevenueStatistics(result, accommodation));
        }
        revenueStatisticsRepository.saveAll(revenueStatistics);
        log.info("최근 일주일 일자별 매출 통계 생성 성공. 총 {}건.", results.size());

        // 3. 일주일간의 매출 유형벌 합계로 성장률을 구하고 매출 합계 통계에 저장한다.
        List<RevenueTotal> revenueTotals = new ArrayList<>();
        for (var totals: revenueTotal.entrySet()) {
            RevenueTotal revenueSum = getRevenueSum(totals);
            revenueTotals.add(revenueSum);
        }
        revenueTotalRepository.saveAll(revenueTotals);
        log.info("매출 합계 통계 생성 성공. 총 {}건.", revenueTotals.size());
    }

    private static RevenueTotal getRevenueSum(Entry<Accommodation, long[]> totals) {
        Accommodation accommodation = totals.getKey();
        long[] value = totals.getValue();
        long couponTotal = value[0];
        long regularTotal = value[1];
        long difference = couponTotal - regularTotal;
        double growthRate = ((regularTotal + difference) / regularTotal) * 100.0;
        log.info("매출 상승 비율: {}", growthRate);
        RevenueTotal revenueSum = RevenueTotal.builder()
            .accommodation(accommodation)
            .couponTotal(couponTotal)
            .regularTotal(regularTotal)
            .growthRate(growthRate)
            .build();
        return revenueSum;
    }

    private RevenueStatistics createRevenueStatistics(
        RevenueStatisticsInterface result, Accommodation accommodation
    ) {
        return RevenueStatistics.builder()
            .accommodation(accommodation)
            .revenueDate(result.getRevenueDate())
            .couponRevenue(result.getCouponRevenue())
            .regularRevenue(result.getRegularRevenue())
            .build();
    }
}

