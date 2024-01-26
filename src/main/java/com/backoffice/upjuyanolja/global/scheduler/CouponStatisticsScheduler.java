package com.backoffice.upjuyanolja.global.scheduler;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.CouponStatisticsDto;
import com.backoffice.upjuyanolja.domain.coupon.dto.CouponStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponStatisticsRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Transactional
public class CouponStatisticsScheduler {

    private final CouponStatisticsRepository couponStatisticsRepository;
    private final AccommodationRepository accommodationRepository;

    // 매일 새벽 2시에 통계 쿼리 실행
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
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
        return CouponStatisticsDto.toEntity(
            accommodation,
            statistics.getTotal(),
            statistics.getUsed(),
            statistics.getStock()
        );
    }

}
