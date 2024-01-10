package com.backoffice.upjuyanolja.domain.reservation.controller;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetCanceledResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationService;
import com.backoffice.upjuyanolja.global.common.ApiResponse;
import com.backoffice.upjuyanolja.global.common.ApiResponse.SuccessResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final SecurityUtil securityUtil;
  private final MemberGetService memberGetService;
  private final ReservationService reservationService;

  @GetMapping()
  public ResponseEntity<SuccessResponse<GetReservedResponse>> getReserved(
      @PageableDefault(size = 3) Pageable pageable
  ) {
    Member currentMember = getCurrentMember();
    GetReservedResponse response = reservationService.getReserved(currentMember, pageable);

    return ApiResponse.success(HttpStatus.OK,
        SuccessResponse.<GetReservedResponse>builder()
            .message("예약 조회에 성공하였습니다.")
            .data(response)
            .build());
  }

  @GetMapping("/cancel")
  public ResponseEntity<SuccessResponse<GetCanceledResponse>> getCanceled(
      @PageableDefault(size = 3) Pageable pageable
  ) {
    Member currentMember = getCurrentMember();
    GetCanceledResponse response = reservationService.getCanceled(currentMember, pageable);

    return ApiResponse.success(HttpStatus.OK,
        SuccessResponse.<GetCanceledResponse>builder()
            .message("예약 취소 조회에 성공하였습니다.")
            .data(response)
            .build());
  }

  private Member getCurrentMember() {
    Long memberId = securityUtil.getCurrentMemberId();
    return memberGetService.getMemberById(memberId);
  }
}
