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
public class RoomOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 옵션 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("에어컨 여부")
    private Boolean airCondition;

    @Column(nullable = false)
    @Comment("TV 여부")
    private Boolean tv;

    @Column(nullable = false)
    @Comment("인터넷 여부")
    private Boolean internet;

    @Builder
    public RoomOption(
        Long id,
        Boolean airCondition,
        Boolean tv,
        Boolean internet
    ) {
        this.id = id;
        this.airCondition = airCondition;
        this.tv = tv;
        this.internet = internet;
    }
}
