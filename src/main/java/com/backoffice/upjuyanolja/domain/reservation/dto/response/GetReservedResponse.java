package com.backoffice.upjuyanolja.domain.reservation.dto.response;

import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import java.util.List;
import org.springframework.data.domain.Page;

public class GetReservedResponse extends GetReservationResponse {

    public GetReservedResponse(Page<Reservation> reservations, List<Payment> payments) {
        super(reservations, payments);
    }

}
