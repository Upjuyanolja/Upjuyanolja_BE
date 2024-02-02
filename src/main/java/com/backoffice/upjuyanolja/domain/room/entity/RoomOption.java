package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase.RoomOptionUpdate;
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
public class RoomOption extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 옵션 식별자")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 식별자")
    private Room room;

    @Column(nullable = false)
    @Comment("에어컨 여부")
    private boolean airCondition;

    @Column(nullable = false)
    @Comment("TV 여부")
    private boolean tv;

    @Column(nullable = false)
    @Comment("인터넷 여부")
    private boolean internet;

    @Builder
    public RoomOption(
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

    public void updateRoomOption(RoomOptionUpdate request) {
        this.airCondition = request.airCondition();
        this.tv = request.tv();
        this.internet = request.internet();
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

}
