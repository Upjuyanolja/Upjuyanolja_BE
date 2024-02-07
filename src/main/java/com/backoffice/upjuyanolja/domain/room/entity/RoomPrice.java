package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomPrice extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 가격 식별자")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 식별자")
    private Room room;

    @Column(nullable = false)
    @Comment("비수기 주중 최소 가격")
    private int offWeekDaysMinFee;

    @Column(nullable = false)
    @Comment("비수기 주말 최소 가격")
    private int offWeekendMinFee;

    @Column(nullable = false)
    @Comment("성수기 주중 최소 가격")
    private int peakWeekDaysMinFee;

    @Column(nullable = false)
    @Comment("성수기 주말 최소 가격")
    private int peakWeekendMinFee;

    @Builder
    private RoomPrice(
        Long id,
        Room room,
        int offWeekDaysMinFee,
        int offWeekendMinFee,
        int peakWeekDaysMinFee,
        int peakWeekendMinFee
    ) {
        this.id = id;
        this.room = room;
        this.offWeekDaysMinFee = offWeekDaysMinFee;
        this.offWeekendMinFee = offWeekendMinFee;
        this.peakWeekDaysMinFee = peakWeekDaysMinFee;
        this.peakWeekendMinFee = peakWeekendMinFee;
    }

    public void updateRoomPrice(int price) {
        this.offWeekDaysMinFee = price;
        this.offWeekendMinFee = price;
        this.peakWeekDaysMinFee = price;
        this.peakWeekendMinFee = price;
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

}
