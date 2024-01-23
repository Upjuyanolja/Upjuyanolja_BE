package com.backoffice.upjuyanolja.domain.reservation.controller;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.reservation.dto.request.CreateReservationRequest;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetCanceledResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservationResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationService;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.backoffice.upjuyanolja.global.validator.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<SuccessResponse<Object>> create(
        @Valid @RequestBody CreateReservationRequest request
    ) {
        Member currentMember = getCurrentMember();
        reservationService.create(currentMember, request);

        return ApiResponse.success(HttpStatus.CREATED,
            SuccessResponse.builder()
                .message("예약이 완료되었습니다.")
                .data(null)
                .build());
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<SuccessResponse<Object>> cancel(
        @ValidId @PathVariable(name = "reservationId") Long reservationId
    ) {
        Member currentMember = getCurrentMember();
        reservationService.cancel(currentMember, reservationId);

        return ApiResponse.success(HttpStatus.NO_CONTENT,
            SuccessResponse.builder()
                .message("성공적으로 예약을 취소했습니다.")
                .data(null)
                .build());
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<GetReservationResponse>> getReserved(
        @RequestParam(name = "status", defaultValue = "RESERVED") ReservationStatus status,
        @PageableDefault(size = 3) Pageable pageable
    ) {
        Member currentMember = getCurrentMember();

        ResponseEntity<SuccessResponse<GetReservationResponse>> success;
        switch (status) {
            case RESERVED, SERVICED -> {
                GetReservedResponse response = reservationService.getReserved(currentMember,
                    pageable);

                success = ApiResponse.success(HttpStatus.OK,
                    SuccessResponse.<GetReservationResponse>builder()
                        .message("예약 조회에 성공하였습니다.")
                        .data(response)
                        .build());
            }
            case CANCELLED -> {
                GetCanceledResponse response = reservationService.getCanceled(currentMember,
                    pageable);

                success = ApiResponse.success(HttpStatus.OK,
                    SuccessResponse.<GetReservationResponse>builder()
                        .message("예약 취소 조회에 성공하였습니다.")
                        .data(response)
                        .build());
            }
            default -> throw new IllegalStateException("Unexpected value: " + status);
        }

        return success;
    }

    private Member getCurrentMember() {
        Long memberId = securityUtil.getCurrentMemberId();
        return memberGetService.getMemberById(memberId);
    }
}
