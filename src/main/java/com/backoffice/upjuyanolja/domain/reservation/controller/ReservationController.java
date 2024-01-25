package com.backoffice.upjuyanolja.domain.reservation.controller;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.reservation.dto.request.CreateReservationRequest;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservationResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationService;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.backoffice.upjuyanolja.global.validator.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final SecurityUtil securityUtil;
    private final MemberGetService memberGetService;
    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<Object> create(
        @Valid @RequestBody CreateReservationRequest request
    ) {
        Member currentMember = getCurrentMember();
        reservationService.create(currentMember, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Object> cancel(
        @ValidId @PathVariable(name = "reservationId") Long reservationId
    ) {
        Member currentMember = getCurrentMember();
        reservationService.cancel(currentMember, reservationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @GetMapping()
    public ResponseEntity<GetReservationResponse> getReserved(
        @RequestParam(name = "status", defaultValue = "RESERVED") ReservationStatus status,
        @PageableDefault(
            size = 3, sort = {"updatedAt", "createdAt"}, direction = Direction.DESC
        ) Pageable pageable
    ) {
        Member currentMember = getCurrentMember();

        GetReservationResponse response;
        switch (status) {
            case RESERVED, SERVICED ->
                response = reservationService.getReserved(currentMember, pageable);
            case CANCELLED -> response = reservationService.getCanceled(currentMember, pageable);
            default -> throw new IllegalStateException("Unexpected value: " + status);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private Member getCurrentMember() {
        Long memberId = securityUtil.getCurrentMemberId();
        return memberGetService.getMemberById(memberId);
    }
}
