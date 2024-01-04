package com.backoffice.upjuyanolja.domain.coupon.entity;

import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.global.common.BaseTime;
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
public class CouponRedeem extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("쿠폰 사용 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    @Comment("쿠폰 식별자")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @Comment("결제 식별자")
    private Payment payment;

    @Column(nullable = false, name = "coupon_balance")
    @Comment("쿠폰 잔여수량")
    private Integer couponBalance;

    @Builder
    public CouponRedeem(
        Long id,
        Coupon coupon,
        Payment payment,
        Integer couponBalance
    ) {
        this.id = id;
        this.coupon = coupon;
        this.payment = payment;
        this.couponBalance = couponBalance;
    }
}