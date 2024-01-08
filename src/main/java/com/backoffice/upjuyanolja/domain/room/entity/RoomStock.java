package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomStock {
    /*
    * @Todo 객실 재고 엔티티 논의 필요
    *  1. 객실 재고는 날짜 별로 저장 되어야 하는가?
    *  2. 예약 시 객실 가격은 어디서 참조 하는가? 재고 등록시 가격을 저장 하는가?
    *  */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 재고 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room_id")
    @Comment("객실 식별자")
    private Room room;

    @Column(nullable = false)
    @Comment("개수")
    private int count;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("상태")
    private RoomStatus status;

    @Column(nullable = false)
    @Comment("적용일시")
    private LocalDateTime applyDate;

    @Column(nullable = true)
    @Comment("종료일시")
    private LocalDateTime stopDate;

    @Builder
    public RoomStock(Long id,
        Room room,
        int count,
        RoomStatus status,
        LocalDateTime applyDate,
        LocalDateTime stopDate
    ) {
        this.id = id;
        this.room = room;
        this.count = count;
        this.status = status;
        this.applyDate = applyDate;
        this.stopDate = stopDate;
    }
}
