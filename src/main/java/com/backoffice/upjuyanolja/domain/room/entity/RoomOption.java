package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase.RoomOptionUpdateDto;
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
 * 객실 옵션 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomOption {

    /**
     * 객실 옵션 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 옵션 식별자")
    private Long id;

    /**
     * 객실 Entity
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 식별자")
    private Room room;

    /**
     * 객실 에어컨 여부
     */
    @Column(nullable = false)
    @Comment("에어컨 여부")
    private boolean airCondition;

    /**
     * 객실 TV 여부
     */
    @Column(nullable = false)
    @Comment("TV 여부")
    private boolean tv;

    /**
     * 객실 인터넷 여부
     */
    @Column(nullable = false)
    @Comment("인터넷 여부")
    private boolean internet;

    /**
     * 객실 옵션 Entity Builder
     *
     * @param id           객실 옵션 식별자
     * @param room         객실 Entity
     * @param airCondition 객실 에어컨 여부
     * @param tv           객실 TV 여부
     * @param internet     객실 인터넷 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    private RoomOption(
        Long id,
        Room room,
        boolean airCondition,
        boolean tv,
        boolean internet
    ) {
        this.id = id;
        this.room = room;
        this.airCondition = airCondition;
        this.tv = tv;
        this.internet = internet;
    }

    /**
     * 객실 옵션 수정
     *
     * @param roomOptionUpdateDto 객실 옵션 수정 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public void update(RoomOptionUpdateDto roomOptionUpdateDto) {
        this.airCondition = roomOptionUpdateDto.airCondition();
        this.tv = roomOptionUpdateDto.tv();
        this.internet = roomOptionUpdateDto.internet();
    }
}
