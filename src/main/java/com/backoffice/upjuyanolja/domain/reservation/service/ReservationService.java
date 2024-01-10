package com.backoffice.upjuyanolja.domain.reservation.service;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;

  public GetReservedResponse getReserved(Member currentMember, Pageable pageable) {
    List<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
        currentMember,
        Arrays.asList(ReservationStatus.RESERVED, ReservationStatus.SERVICED),
        pageable
    );
    return new GetReservedResponse(reservations);
  }
}
