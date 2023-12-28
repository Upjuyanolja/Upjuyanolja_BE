package com.backoffice.upjuyanolja.domain.reservation.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Room;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
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
public class ReservationRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("숙소 식별자")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    @Comment("예약 식별자")
    private Reservation reservation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @Comment("객실 식별자")
    private Room room;
    @Column(nullable = false)
    @Comment("가격")
    private int price;
    @Column(nullable = false)
    @Comment("숙박 시작일")
    private LocalDate startDate;
    @Column(nullable = false)
    @Comment("숙박 마지막일")
    private LocalDate endDate;
    @Column(nullable = false)
    @Comment("방문자명")
    private String visitorName;
    @Column(nullable = false)
    @Comment("방문자 전화번호")
    private String visitorPhone;

    @Builder
    public ReservationRoom(
        Long id,
        Reservation reservation,
        Room room,
        int price,
        LocalDate startDate,
        LocalDate endDate,
        String visitorName,
        String visitorPhone
    ) {
        this.id = id;
        this.reservation = reservation;
        this.room = room;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visitorName = visitorName;
        this.visitorPhone = visitorPhone;
    }
}
