package com.backoffice.upjuyanolja.domain.room.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 객실 가격 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomPrice {

    /**
     * 객실 가격 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 가격 식별자")
    private Long id;

    /**
     * 객실 Entity
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 식별자")
    private Room room;

    /**
     * 객실 비수기 주중 최소 가격
     */
    @Column(nullable = false)
    @Comment("비수기 주중 최소 가격")
    private int offWeekDaysMinFee;

    /**
     * 객실 비수기 주말 최소 가격
     */
    @Column(nullable = false)
    @Comment("비수기 주말 최소 가격")
    private int offWeekendMinFee;

    /**
     * 객실 성수기 주중 최소 가격
     */
    @Column(nullable = false)
    @Comment("성수기 주중 최소 가격")
    private int peakWeekDaysMinFee;

    /**
     * 객실 성수기 주말 최소 가격
     */
    @Column(nullable = false)
    @Comment("성수기 주말 최소 가격")
    private int peakWeekendMinFee;

    /**
     * 객실 가격 Entity Builder
     *
     * @param id                 객실 가격 식별자
     * @param room               객실 Entity
     * @param offWeekDaysMinFee  객실 비수기 주중 최소 가격
     * @param offWeekendMinFee   객실 비수기 주말 최소 가격
     * @param peakWeekDaysMinFee 객실 성수기 주중 최소 가격
     * @param peakWeekendMinFee  객실 성수기 주말 최소 가격
     * @author JeongUijeong (jeong275117@gmail.com)
     */
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

    /**
     * 객실 가격 수정 메서드
     *
     * @param price 객실 수정가
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public void updateRoomPrice(int price) {
        this.offWeekDaysMinFee = price;
        this.offWeekendMinFee = price;
        this.peakWeekDaysMinFee = price;
        this.peakWeekendMinFee = price;
    }
}
