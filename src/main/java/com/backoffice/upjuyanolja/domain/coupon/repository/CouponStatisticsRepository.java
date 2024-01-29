package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.statistics.CouponStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponStatisticsRepository extends JpaRepository<CouponStatistics, Long> {

    String couponQuery = """
        select t.id,
               coalesce(t.total, 0)                   as total,
               coalesce(u.used, 0)                    as used,
               coalesce(t.total - u.used, t.total, 0) as stock
        from (select ac.id, coalesce(sum(ci.quantity), 0) as total
              from accommodation ac
                       join room rm on ac.id = rm.accommodation_id
                       left join coupon_issuance ci on rm.id = ci.room_id
                       left join coupon cp on ci.coupon_id = cp.id
              where cp.deleted_at is null
              group by ac.id) t
                 left join
             (select ac.id, coalesce(count(*), 0) as used
              from accommodation ac
                       join room rm on ac.id = rm.accommodation_id
                       left join reservation_room rr on rm.id = rr.room_id
                       left join reservation rv on rr.id = rv.reservation_room_id
              where rv.is_coupon_used = true
                and rv.status != 'CANCELLED'
              group by ac.id) u
             on t.id = u.id
        """;
    @Query(value = couponQuery, nativeQuery = true)
    List<CouponStatisticsInterface> createStatistics();

    Optional<CouponStatistics> findByAccommodationId(Long accommodationId);
}
