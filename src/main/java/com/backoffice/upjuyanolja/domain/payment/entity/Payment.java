package com.backoffice.upjuyanolja.domain.payment.entity;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
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
public class Payment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("결제 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "member_id")
    @Comment("회원 식별자")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    @Comment("결제 식별자")
    private Reservation reservation;

    @Column(nullable = false, name = "pay_method")
    @Enumerated(value = EnumType.STRING)
    @Comment("결제 수단")
    private PayMethod payMethod;

    @Column(nullable = false, name = "room_price")
    @Comment("객실 가격")
    private Integer roomPrice;

    @Column(nullable = false, name = "discount_amount")
    @Comment("할인 금액")
    private Integer discountAmount;

    @Column(nullable = false, name = "total_amount")
    @Comment("결제 금액")
    private Integer totalAmount;

    @Builder
    public Payment(
        Long id,
        Member member,
        Reservation reservation,
        PayMethod payMethod,
        Integer roomPrice,
        Integer discountAmount,
        Integer totalAmount
    ) {
        this.id = id;
        this.member = member;
        this.reservation = reservation;
        this.payMethod = payMethod;
        this.roomPrice = roomPrice;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
    }
}
