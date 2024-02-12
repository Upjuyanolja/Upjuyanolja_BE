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

/**
 * 객실 재고 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 * @author Chaewon12 (djacltk@naver.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomStock {

    /**
     * 객실 재고 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 재고 식별자")
    private Long id;

    /**
     * 객실 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room_id")
    @Comment("객실 식별자")
    private Room room;

    /**
     * 객실 재고
     */
    @Column(nullable = false)
    @Comment("객실 재고")
    private int count;

    /**
     * 객실 재고 기준일
     */
    @Comment("날짜")
    private LocalDate date;

    /**
     * 객실 재고 Entity Builder
     *
     * @param id    객실 재고 식별자
     * @param room  객실 Entity
     * @param count 객실 재고
     * @param date  객실 재고 기준일
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    private RoomStock(
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

    /**
     * 객실 재고 증가 메서드
     *
     * @param quantity 객실 재고 증가 수량
     * @author Chaewon12 (djacltk@naver.com)
     */
    public void increase(int quantity) {
        this.count += quantity;
    }

    /**
     * 객실 재고 감소 메서드
     *
     * @param quantity 객실 재고 감소 수량
     * @author Chaewon12 (djacltk@naver.com)
     */
    public void decrease(int quantity) {
        if (this.count - quantity < 0) {
            throw new IllegalArgumentException();
        }

        this.count -= quantity;
    }

    /**
     * 객실 재고 수정 메서드
     *
     * @param quantity 객실 수정 재고량
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public void update(int quantity) {
        if (this.count < quantity) {
            this.count = this.count + quantity;
        } else {
            this.count = Math.max(this.count - quantity, 0);
        }
    }
}
