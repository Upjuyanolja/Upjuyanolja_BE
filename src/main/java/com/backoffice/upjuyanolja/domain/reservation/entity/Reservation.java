package com.backoffice.upjuyanolja.domain.reservation.entity;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.global.common.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room_id")
    @Comment("객실 식별자")
    private RoomStock roomStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "payment_id")
    @Comment("결제 식별자")
    private Payment payment;

    @Column(nullable = false, name = "visitor_name")
    @Comment("방문자 성함")
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

    @Column(nullable = false, name = "cancelled")
    @Comment("예약 취소 여부")
    private Boolean cancelled; // 기본값 false / true면 예약 취소

    @Builder
    public Reservation(
        Long id,
        Member member,
        RoomStock roomStock,
        Payment payment,
        String visitorName,
        String visitorPhone,
        LocalDate startDate,
        LocalDate endDate,
        Boolean cancelled
    ) {
        this.id = id;
        this.member = member;
        this.roomStock = roomStock;
        this.payment = payment;
        this.visitorName = visitorName;
        this.visitorPhone = visitorPhone;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancelled = cancelled;
    }
}
