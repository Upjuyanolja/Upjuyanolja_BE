package com.backoffice.upjuyanolja.domain.reservation.repository;

import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
