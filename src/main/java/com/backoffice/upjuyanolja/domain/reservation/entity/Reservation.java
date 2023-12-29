package com.backoffice.upjuyanolja.domain.reservation.entity;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.global.common.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "coupon_id")
    @Comment("쿠폰 식별자")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @Comment("결제 식별자")
    private Payment payment;

    @Column(nullable = false, name = "visitor_name")
    @Comment("방문자명")
    private String visitorName;

    @Column(nullable = false, name = "visitor_phone")
    @Comment("방문자 전화번호")
    private String visitorPhone;

    @Column(nullable = false, name = "start_date")
    @Comment("입실 일자")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    @Comment("퇴실 일자")
    private LocalDate endDate;

    @Builder
    public Reservation(Long id,
        Member member,
        Coupon coupon,
        Payment payment,
        String visitorName,
        String visitorPhone,
        LocalDate startDate,
        LocalDate endDate)
    {
        this.id = id;
        this.member = member;
        this.coupon = coupon;
        this.payment = payment;
        this.visitorName = visitorName;
        this.visitorPhone = visitorPhone;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
