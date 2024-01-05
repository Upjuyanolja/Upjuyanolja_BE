package com.backoffice.upjuyanolja.domain.reservation.entity;

import com.backoffice.upjuyanolja.global.common.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Boolean cancelled; // true: 예약 취소

    @Builder
    public Reservation(
        Long id,
        String visitorName,
        String visitorPhone,
        LocalDate startDate,
        LocalDate endDate,
        Boolean cancelled
    ) {
        this.id = id;
        this.visitorName = visitorName;
        this.visitorPhone = visitorPhone;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancelled = cancelled;
    }
}
