package com.backoffice.upjuyanolja.domain.reservation.repository;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  Page<Reservation> findAllByMemberAndStatusIn(
      Member currentMember,
      Collection<ReservationStatus> statuses,
      Pageable pageable
  );

  Optional<Reservation> findByIdAndMember(Long reservationId, Member currentMember);
}
