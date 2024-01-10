package com.backoffice.upjuyanolja.domain.reservation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationRoom;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationRoomException;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;

@Getter
public class GetReservedResponse {

  private final List<ReservationDTO> reservations;

  public GetReservedResponse(List<Reservation> reservations) {
    this.reservations = reservations.stream().map(ReservationDTO::new).toList();
  }

  @Getter
  public class ReservationDTO {

    private final Long id;
    private final LocalDate date;
    private final boolean isCouponUsed;
    private final int roomPrice;
    private final int totalAmount;
    private final Long accommodationId;
    private final String accommodationName;
    private final Long roomId;
    private final String roomName;
    private final LocalTime checkInTime;
    private final LocalTime checkOutTime;
    private final int defaultCapacity;
    private final int maxCapacity;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ReservationStatus status;

    public ReservationDTO(Reservation reservation) {
      Payment payment = reservation.getPayment();

      this.id = reservation.getId();
      this.date = LocalDate.from(reservation.getCreatedAt());
      this.isCouponUsed = reservation.getIsCouponUsed();
      this.roomPrice = payment.getRoomPrice();
      this.totalAmount = payment.getTotalAmount();
      this.status = reservation.getStatus();

      try {
        ReservationRoom reservationRoom = reservation.getReservationRoom();
        Room room = reservationRoom.getRoom();
        Accommodation accommodation = room.getAccommodation();

        this.accommodationId = accommodation.getId();
        this.accommodationName = accommodation.getName();
        this.roomId = room.getId();
        this.roomName = room.getName();
        this.checkInTime = room.getCheckIn();
        this.checkOutTime = room.getCheckOut();
        this.defaultCapacity = room.getStandard();
        this.maxCapacity = room.getCapacity();
        this.startDate = reservationRoom.getStartDate();
        this.endDate = reservationRoom.getEndDate();

      } catch (NullPointerException e) {
        throw new NoSuchReservationRoomException();
      }
    }
  }
}
