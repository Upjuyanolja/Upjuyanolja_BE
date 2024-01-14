package com.backoffice.upjuyanolja.domain.reservation.service;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.reservation.dto.request.CreateReservationRequest;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetCanceledResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;

  public void create(Member currentMember, CreateReservationRequest request) {

  }

  @Transactional(readOnly = true)
  public GetReservedResponse getReserved(Member currentMember, Pageable pageable) {
    Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
        currentMember,
        Arrays.asList(ReservationStatus.RESERVED, ReservationStatus.SERVICED),
        pageable
    );
    return new GetReservedResponse(reservations);
  }

  @Transactional(readOnly = true)
  public GetCanceledResponse getCanceled(Member currentMember, Pageable pageable) {
    Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
        currentMember,
        List.of(ReservationStatus.CANCELLED),
        pageable
    );
    return new GetCanceledResponse(reservations);
  }
}
