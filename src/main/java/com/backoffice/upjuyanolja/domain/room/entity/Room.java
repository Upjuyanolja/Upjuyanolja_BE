package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase.RoomUpdateDto;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import com.backoffice.upjuyanolja.global.util.DateTimeParser;
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
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 객실 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Room extends BaseTime {

    /**
     * 객실 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 식별자")
    private Long id;

    /**
     * 객실이 속한 숙소 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    /**
     * 객실 이름
     */
    @Column(nullable = false)
    @Comment("객실 이름")
    private String name;

    /**
     * 객실 상태
     */
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("객실 상태")
    private RoomStatus status;

    /**
     * 객실 개수
     */
    @Column(nullable = false)
    @Comment("객실 개수")
    private int amount;

    /**
     * 객실 기준 인원
     */
    @Column(columnDefinition = "TINYINT")
    @Comment("객실 기준 인원")
    private int defaultCapacity;

    /**
     * 객실 최대 인원
     */
    @Column(columnDefinition = "TINYINT")
    @Comment("객실 최대 인원")
    private int maxCapacity;

    /**
     * 객실 체크인 시간
     */
    @Column(columnDefinition = "TIME")
    @Comment("객실 체크인 시간")
    private LocalTime checkInTime;

    /**
     * 객실 체크아웃 시간
     */
    @Column(columnDefinition = "TIME")
    @Comment("객실 체크아웃 시간")
    private LocalTime checkOutTime;

    /**
     * 객실 Entity Builder
     *
     * @param id              객실 식별자
     * @param accommodation   객실이 속한 숙소 Entity
     * @param name            객실 이름
     * @param status          객실 상태
     * @param amount          객실 개수
     * @param defaultCapacity 객실 기본 인원
     * @param maxCapacity     객실 최대 인원
     * @param checkInTime     객실 체크인 시간
     * @param checkOutTime    객실 체크아웃 시간
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    private Room(
        Long id,
        Accommodation accommodation,
        String name,
        int defaultCapacity,
        int maxCapacity,
        int amount,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        RoomStatus status
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.name = name;
        this.defaultCapacity = defaultCapacity;
        this.maxCapacity = maxCapacity;
        this.amount = amount;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
    }

    /**
     * 객실 수정 메서드
     *
     * @param roomUpdateDto 객실 수정 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public void update(RoomUpdateDto roomUpdateDto) {
        this.name = roomUpdateDto.name();
        this.status = RoomStatus.valueOf(roomUpdateDto.status());
        this.defaultCapacity = roomUpdateDto.defaultCapacity();
        this.maxCapacity = roomUpdateDto.maxCapacity();
        this.checkInTime = DateTimeParser.timeParser(roomUpdateDto.checkInTime());
        this.checkOutTime = DateTimeParser.timeParser(roomUpdateDto.checkOutTime());
        this.amount = roomUpdateDto.amount();
    }

    /**
     * 객실 논리 삭제 메서드 재정의
     *
     * @param currentTime 삭제 시점의 현재 시간
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }
}
