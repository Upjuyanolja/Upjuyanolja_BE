package com.backoffice.upjuyanolja.domain.reservation.entity;

import lombok.Getter;

@Getter
public enum ReservationStatus {
  RESERVED("예약 완료"),
  CANCELLED("예약 취소"),
  SERVICED("사용 완료");

  private final String label;

  ReservationStatus(String label) {
    this.label = label;
  }
}
