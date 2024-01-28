package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.statistics.CouponStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponStatisticsRepository extends JpaRepository<CouponStatistics, Long> {

    String couponQuery = """
        select t.id, t.total, u.used, t.total - u.used as stock
        from (select ac.id as id, sum(ci.quantity) as total
              from coupon_issuance ci
                       left join room rm on ci.room_id = rm.id
                       left join coupon cp on cp.id = ci.coupon_id
                       left join accommodation ac on rm.accommodation_id = ac.id
              where rm.deleted_at is null
                and ac.deleted_at is null
                and cp.deleted_at is null
              group by ac.id) t
                 inner join
             (select ac.id as id, count(*) as used
              from room rm
                       left join reservation_room rr on rr.room_id = rm.id
                       left join reservation rv on rr.id = rv.reservation_room_id
                       left join coupon_redeem cr on rv.id = cr.reservation_id
                       left join accommodation ac on rm.accommodation_id = ac.id
              where rv.is_coupon_used = true
                and rm.deleted_at is null
                and ac.deleted_at is null
                and not exists(select 1
                               from coupon cp
                                        inner join coupon_redeem cr on cr.coupon_id = cp.id
                               where cp.deleted_at is not null)
              group by ac.id) u
             on t.id = u.id
        """;
    @Query(value = couponQuery, nativeQuery = true)
    List<CouponStatisticsInterface> createStatistics();

    Optional<CouponStatistics> findByAccommodationId(Long accommodationId);
}
