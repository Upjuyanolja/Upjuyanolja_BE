package com.backoffice.upjuyanolja.domain.payment.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum PayMethod {

  CREDIT_CARD,
  KAKAO_PAY,
  TOSS_PAY,
  NAVER_PAY,
  PAYPAL;

  /*
   * Json 데이터를 역직렬화 하는 과정을 수동 설정
   * - 정의 하지 않은 값이 들어오면 null 을 반환
   * - null 이 반환되면 validation으로 설정한 @NotNull에 의해 exception 발생
   * */
  @JsonCreator
  public static PayMethod parsing(String inputValue) {
    return Stream.of(PayMethod.values())
        .filter(payMethod -> payMethod.toString().equals(inputValue))
        .findFirst()
        .orElse(null);
  }
}
