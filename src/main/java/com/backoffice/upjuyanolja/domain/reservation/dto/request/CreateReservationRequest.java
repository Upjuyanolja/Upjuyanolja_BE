package com.backoffice.upjuyanolja.domain.reservation.dto.request;

import com.backoffice.upjuyanolja.domain.payment.entity.PayMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateReservationRequest {

  @NotNull(message = "객실 Id를 입력하세요.")
  Long roomId;

  @NotBlank(message = "방문자 성함을 입력해주세요")
  @Size(min = 2, max = 12, message = "올바른 이름을 입력해주세요")
  String visitorName;

  @NotBlank(message = "방문자 전화번호를 입력해주세요")
  @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
  String visitorPhone;

  @NotNull(message = "입실 일자를 입력하세요")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  LocalDate startDate;

  @NotNull(message = "퇴실 일자를 입력하세요")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  LocalDate endDate;

  // null-able
  Long couponId;

  @NotNull(message = "결제 금액을 입력하세요.")
  Integer totalPrice;

  @NotNull(message = "유효하지 않은 결제 수단 입니다.")
  PayMethod payMethod;
}
