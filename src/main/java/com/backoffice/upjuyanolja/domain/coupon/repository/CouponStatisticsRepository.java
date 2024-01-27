package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.statistics.CouponStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponStatisticsRepository extends JpaRepository<CouponStatistics, Long> {

    @Query(value =
        "select t.id, "
        + "       t.total, "
        + "       u.used, "
        + "       t.total - u.used   as stock "
        + "from (select ac.id as id, sum(ci.QUANTITY) as total "
        + "      from coupon_issuance ci "
        + "               join room rm on ci.ROOM_ID = rm.id "
        + "               join accommodation ac on rm.accommodation_id = ac.id "
        + "      group by ac.id) t "
        + "         inner join "
        + "     (select ac.id as id, count(*) as used "
        + "      from reservation_room rr "
        + "               left join reservation rv on rr.id = rv.reservation_room_id "
        + "               left join coupon_redeem cr on rv.id = cr.reservation_id "
        + "               left join room r on rr.room_id = r.ID "
        + "               left join accommodation ac on r.accommodation_id = ac.id "
        + "      where rv.is_coupon_used = true "
        + "      group by ac.id) u "
        + "     on t.id = u.id", nativeQuery = true)
    List<CouponStatisticsInterface> createStatistics();

    Optional<CouponStatistics> findByAccommodationId(Long accommodationId);
}
