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
public class AccommodationOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("숙소 옵션 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("객실 내 취사 여부")
    private Boolean cooking;

    @Column(nullable = false)
    @Comment("주차 시설 여부")
    private Boolean parking;

    @Column(nullable = false)
    @Comment("픽업 서비스 여부")
    private Boolean pickup;

    @Column(nullable = false)
    @Comment("바비큐 장 여부")
    private Boolean barbecue;

    @Column(nullable = false)
    @Comment("피트니스 센터 여부")
    private Boolean fitness;

    @Column(nullable = false)
    @Comment("노래방 여부")
    private Boolean karaoke;

    @Column(nullable = false)
    @Comment("사우나 여부")
    private Boolean sauna;

    @Column(nullable = false)
    @Comment("스포츠 시설 여부")
    private Boolean sports;

    @Column(nullable = false)
    @Comment("세미나 실 여부")
    private Boolean seminar;

    @Builder
    public AccommodationOption(
        Long id,
        Boolean cooking,
        Boolean parking,
        Boolean pickup,
        Boolean barbecue,
        Boolean fitness,
        Boolean karaoke,
        Boolean sauna,
        Boolean sports,
        Boolean seminar
    ) {
        this.id = id;
        this.cooking = cooking;
        this.parking = parking;
        this.pickup = pickup;
        this.barbecue = barbecue;
        this.fitness = fitness;
        this.karaoke = karaoke;
        this.sauna = sauna;
        this.sports = sports;
        this.seminar = seminar;
    }
}
