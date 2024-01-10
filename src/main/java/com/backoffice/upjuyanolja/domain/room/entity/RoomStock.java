package com.backoffice.upjuyanolja.domain.room.entity;

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
    @Comment("객실 재고")
    private int count;

    @Comment("날짜")
    private LocalDate date;

    @Builder
    public RoomStock(
        Long id,
        Room room,
        int count,
        LocalDate date
    ) {
        this.id = id;
        this.room = room;
        this.count = count;
        this.date = date;
    }
}
