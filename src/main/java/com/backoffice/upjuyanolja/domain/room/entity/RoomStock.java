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
    @Comment("개수")
    private int count;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("상태")
    private RoomStatus status;

    @Column(nullable = false)
    @Comment("적용일시")
    private LocalDateTime applyDate;

    @Column(nullable = false)
    @Comment("종료일시")
    private LocalDateTime stopDate;

    @Builder
    public RoomStock(
        Long id,
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
