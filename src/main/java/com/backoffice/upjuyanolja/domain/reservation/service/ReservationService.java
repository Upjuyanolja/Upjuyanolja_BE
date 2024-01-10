package com.backoffice.upjuyanolja.domain.reservation.service;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.reservation.dto.request.CreateReservationRequest;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.CreateReservationResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;

  // @Todo 객실 재고 테이블 확정 후 구현(동시성 제어)
  public CreateReservationResponse create(Member currentMember, CreateReservationRequest request) {
    return null;
  }

  public GetReservedResponse getReserved(Member currentMember, Pageable pageable) {
    Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
        currentMember,
        Arrays.asList(ReservationStatus.RESERVED, ReservationStatus.SERVICED),
        pageable
    );
    return new GetReservedResponse(reservations);
  }
}
