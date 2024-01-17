package com.backoffice.upjuyanolja.domain.reservation.entity;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reservation extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("예약 식별자")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "member_id")
  @Comment("회원 식별자")
  private Member member;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "reservation_room_id")
  @Comment("예약 객실 식별자")
  private ReservationRoom reservationRoom;

  @Column(nullable = false, name = "visitor_name")
  @Comment("방문자 성함")
  private String visitorName;

  @Column(nullable = false, name = "visitor_phone")
  @Comment("방문자 전화번호")
  private String visitorPhone;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  @Comment("결제 식별자")
  private Payment payment;

  @Column(nullable = false, name = "is_coupon_used")
  @Comment("쿠폰 사용 여부")
  private Boolean isCouponUsed;

  @Column(nullable = false, name = "status")
  @Enumerated(value = EnumType.STRING)
  @Comment("예약 상태")
  private ReservationStatus status;

  @Builder
  public Reservation(
      Long id,
      Member member,
      ReservationRoom reservationRoom,
      String visitorName,
      String visitorPhone,
      Payment payment,
      Boolean isCouponUsed,
      ReservationStatus status
  ) {
    this.id = id;
    this.member = member;
    this.reservationRoom = reservationRoom;
    this.visitorName = visitorName;
    this.visitorPhone = visitorPhone;
    this.payment = payment;
    this.isCouponUsed = isCouponUsed;
    this.status = status;
  }

  public void updateStatus(ReservationStatus status) {
    this.status = status;
  }
}
