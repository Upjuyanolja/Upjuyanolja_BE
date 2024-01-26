package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.dto.CouponStatisticsInterface;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatistics;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponStatisticsRepository extends JpaRepository<CouponStatistics, Long> {

    @Query(value =
        "select t.id, "
        + "       t.total, "
        + "       u.used, "
        + "       t.total - u.used   as stock "
        + "from (select ac.id as id, sum(ci.QUANTITY) as total "
        + "      from COUPON_ISSUANCE ci "
        + "               join room rm on ci.ROOM_ID = rm.id "
        + "               join ACCOMMODATION ac on rm.accommodation_id = ac.id "
        + "      group by ac.id) t "
        + "         inner join "
        + "     (select ac.id as id, count(*) as used "
        + "      from reservation_room rr "
        + "               left join reservation rv on rr.id = rv.RESERVATION_ROOM_ID "
        + "               left join coupon_redeem cr on rv.id = cr.RESERVATION_ID "
        + "               left join room r on rr.ROOM_ID = r.ID "
        + "               left join accommodation ac on r.ACCOMMODATION_ID = ac.id "
        + "      where rv.IS_COUPON_USED = true "
        + "      group by ac.id) u "
        + "     on t.id = u.id", nativeQuery = true)
    List<CouponStatisticsInterface> createStatistics();
}
