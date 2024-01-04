package com.backoffice.upjuyanolja.domain.room.entity;

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
import java.time.LocalDate;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 재고 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room_id")
    @Comment("객실 식별자")
    private Room room;

    @Column(nullable = false)
    @Comment("객실 개수")
    private int count;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("객실 상태")
    private RoomStatus roomStatus;

    @Column(nullable = false)
    @Comment("적용 일시")
    private LocalDate applyDate;

    @Comment("종료 일시")
    private LocalDate stopDate;

    @Builder
    public RoomStock(
        Long id,
        Room room,
        int count,
        RoomStatus roomStatus,
        LocalDate applyDate,
        LocalDate stopDate
    ) {
        this.id = id;
        this.room = room;
        this.count = count;
        this.roomStatus = roomStatus;
        this.applyDate = applyDate;
        this.stopDate = stopDate;
    }
}
