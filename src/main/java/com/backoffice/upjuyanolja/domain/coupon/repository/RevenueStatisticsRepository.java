package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.statistics.RevenueStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.entity.RevenueStatistics;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RevenueStatisticsRepository extends JpaRepository<RevenueStatistics, Long> {

    String revenueSQL = """
        SELECT ac.id         AS id,
               py.CREATED_AT AS revenueDate,
               COALESCE(SUM(CASE WHEN py.DISCOUNT_AMOUNT = 0 THEN py.TOTAL_AMOUNT ELSE 0 END),
                        0)   AS regularRevenue,
               COALESCE(SUM(CASE WHEN py.DISCOUNT_AMOUNT != 0 THEN py.TOTAL_AMOUNT ELSE 0 END),
                        0)   AS couponRevenue
        FROM RESERVATION_ROOM rr
                 LEFT JOIN RESERVATION rv ON rr.ID = rv.RESERVATION_ROOM_ID
                 LEFT JOIN PAYMENT py ON rv.PAYMENT_ID = py.ID
                 LEFT JOIN ROOM rm ON rr.ROOM_ID = rm.ID
                 LEFT JOIN ACCOMMODATION ac ON rm.ACCOMMODATION_ID = ac.ID
        WHERE py.CREATED_AT BETWEEN :startDate AND :endDate
          AND rv.STATUS = 'SERVICED'
        GROUP BY ac.id, py.CREATED_AT
        ORDER BY id, revenueDate
        """;
    @Query(value = revenueSQL, nativeQuery = true)
    List<RevenueStatisticsInterface> createRevenueStatistics(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<RevenueStatistics> findByAccommodationId(Long accommodationId);
}
