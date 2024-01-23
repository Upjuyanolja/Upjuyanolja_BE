package com.backoffice.upjuyanolja.domain.reservation.dto.response;

import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;

public class GetReservedResponse extends GetReservationResponse {

    public GetReservedResponse(Page<Reservation> reservations) {
        super(reservations);
    }

}
