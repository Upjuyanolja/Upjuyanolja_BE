package com.backoffice.upjuyanolja.domain.accommodation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 가격 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("비수기 주중 최소 가격")
    private Integer offWeekDaysMinFee;

    @Column(nullable = false)
    @Comment("비수기 주말 최소 가격")
    private Integer offWeekendMinFee;

    @Column(nullable = false)
    @Comment("성수기 주중 최소 가격")
    private Integer peakWeekDaysMinFee;

    @Column(nullable = false)
    @Comment("성수기 주말 최소 가격")
    private Integer peakWeekendMinFee;

    @Builder
    private RoomPrice(
        Long id,
        Integer offWeekDaysMinFee,
        Integer offWeekendMinFee,
        Integer peakWeekDaysMinFee,
        Integer peakWeekendMinFee
    ) {
        this.id = id;
        this.offWeekDaysMinFee = offWeekDaysMinFee;
        this.offWeekendMinFee = offWeekendMinFee;
        this.peakWeekDaysMinFee = peakWeekDaysMinFee;
        this.peakWeekendMinFee = peakWeekendMinFee;
    }
}
