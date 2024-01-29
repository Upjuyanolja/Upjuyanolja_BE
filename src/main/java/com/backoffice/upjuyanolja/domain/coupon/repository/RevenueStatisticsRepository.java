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
        select ac.id,
               py.created_at as revenueDate,
               coalesce(sum(case when py.discount_amount = 0 then py.total_amount else 0 end),
                        0)   as regularRevenue,
               coalesce(sum(case when py.discount_amount != 0 then py.total_amount else 0 end),
                        0)   as couponRevenue
        from reservation_room rr
                 left join reservation rv on rr.id = rv.reservation_room_id
                 left join payment py on rv.payment_id = py.id
                 left join room rm on rr.room_id = rm.id
                 left join accommodation ac on rm.accommodation_id = ac.id
                 left join coupon cp on cp.room_id = rm.id
        where py.created_at between :startDate and :endDate
          and rv.status != 'CANCELLED'
          and cp.deleted_at is null
        group by ac.id, date(py.created_at)
        order by ac.id, date(py.created_at);
        """;
    @Query(value = revenueSQL, nativeQuery = true)
    List<RevenueStatisticsInterface> createRevenueStatistics(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<RevenueStatistics> findByAccommodationId(Long accommodationId);
}
