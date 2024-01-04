package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponRoom;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponRoomRepository extends JpaRepository<CouponRoom, Long> {
    @Query(value = "select cr from CouponRoom cr join cr.room r where r.accommodation.id = :accommodationId")
    Optional<List<CouponRoom>> findByAccommodationId(Long accommodationId);

    Optional<List<CouponRoom>> findByRoom(Room room);

}
