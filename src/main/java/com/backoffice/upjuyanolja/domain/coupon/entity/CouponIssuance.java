package com.backoffice.upjuyanolja.domain.coupon.entity;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CouponIssuance extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("쿠폰 발급 식별자")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coupon_id")
  @Comment("쿠폰 식별자")
  private Coupon coupon;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  @Comment("객실 식별자")
  private Room room;

  @Column(nullable = false, name = "coupon_amount")
  @Comment("쿠폰 발급 수량")
  private Integer couponAmount;

  @Column(nullable = false, name = "coupon_balance")
  @Comment("쿠폰 잔여 수량")
  private Integer couponBalance;

  @Builder
  public CouponIssuance(
      Long id,
      Coupon coupon,
      Room room,
      Integer couponAmount,
      Integer couponBalance
  ) {
    this.id = id;
    this.coupon = coupon;
    this.room = room;
    this.couponAmount = couponAmount;
    this.couponBalance = couponBalance;
  }
}
