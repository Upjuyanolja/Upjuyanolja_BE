package com.backoffice.upjuyanolja.domain.reservation.dto.response;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationRoom;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationRoomException;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
public class GetReservationResponse {

    private final Integer pageNum;
    private final Integer pageSize;
    private final Integer totalPages;
    private final Long totalElements;
    private final Boolean isLast;

    private final List<GetReservationResponse.ReservationDTO> reservations;

    public GetReservationResponse(Page<Reservation> reservations, List<Payment> payments) {
        if (reservations == null) {
            this.pageNum = null;
            this.pageSize = null;
            this.totalPages = null;
            this.totalElements = null;
            this.isLast = null;
            this.reservations = null;
        } else {
            Pageable pageable = reservations.getPageable();
            this.pageNum = pageable.getPageNumber();
            this.pageSize = pageable.getPageSize();
            this.totalPages = reservations.getTotalPages();
            this.totalElements = reservations.getTotalElements();
            this.isLast = reservations.isLast();

            List<GetReservationResponse.ReservationDTO> reservationDTOList = new ArrayList<>();
            for (int i = 0; i < reservations.getContent().size(); i++) {
                reservationDTOList.add(
                    new ReservationDTO(reservations.getContent().get(i), payments.get(i)));
            }
            this.reservations = reservationDTOList;
        }
    }

    @Getter
    public class ReservationDTO {

        private final Long id;
        private final LocalDate date;
        private final Boolean isCouponUsed;
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

        public ReservationDTO(Reservation reservation, Payment payment) {
            LocalDateTime createdAt = reservation.getCreatedAt();

            this.id = reservation.getId();
            this.date = createdAt == null ? null : createdAt.toLocalDate();
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
                this.checkInTime = room.getCheckInTime();
                this.checkOutTime = room.getCheckOutTime();
                this.defaultCapacity = room.getDefaultCapacity();
                this.maxCapacity = room.getMaxCapacity();
                this.startDate = reservationRoom.getStartDate();
                this.endDate = reservationRoom.getEndDate();

            } catch (NullPointerException e) {
                throw new NoSuchReservationRoomException();
            }
        }
    }
}
