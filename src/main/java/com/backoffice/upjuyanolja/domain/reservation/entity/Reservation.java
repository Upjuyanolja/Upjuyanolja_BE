package com.backoffice.upjuyanolja.domain.reservation.entity;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.global.common.BaseTime;
import jakarta.persistence.CascadeType;
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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("예약 숙소 식별자")
    private ReservationRoom reservationRoom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "member_id")
    @Comment("회원 식별자")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "coupon_id")
    @Comment("쿠폰 식별자")
    private Coupon coupon;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("결제 수단")
    private PayMethod payMethod;
    @Column(nullable = false)
    @Comment("결제 금액")
    private int price;

    @Builder
    public Reservation(
        Long id,
        ReservationRoom reservationRoom,
        Member member,
        Coupon coupon,
        PayMethod payMethod,
        int price
    ) {
        this.id = id;
        this.reservationRoom = reservationRoom;
        this.member = member;
        this.coupon = coupon;
        this.payMethod = payMethod;
        this.price = price;
    }
}
