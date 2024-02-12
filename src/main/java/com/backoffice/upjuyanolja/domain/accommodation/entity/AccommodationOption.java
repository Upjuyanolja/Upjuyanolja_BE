package com.backoffice.upjuyanolja.domain.accommodation.entity;

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
 * 숙소 옵션 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AccommodationOption {

    /**
     * 숙소 옵션 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("숙소 옵션 식별자")
    private Long id;

    /**
     * 숙소 Entity
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    /**
     * 숙소 객실 내 취사 여부
     */
    @Column(nullable = false)
    @Comment("객실 내 취사 여부")
    private boolean cooking;

    /**
     * 숙소 주차 시설 여부
     */
    @Column(nullable = false)
    @Comment("주차 시설 여부")
    private boolean parking;

    /**
     * 숙소 픽업 서비스 여부
     */
    @Column(nullable = false)
    @Comment("픽업 서비스 여부")
    private boolean pickup;

    /**
     * 숙소 바베큐 장 여부
     */
    @Column(nullable = false)
    @Comment("바베큐 장 여부")
    private boolean barbecue;

    /**
     * 숙소 피트니스 센터 여부
     */
    @Column(nullable = false)
    @Comment("피트니스 센터 여부")
    private boolean fitness;

    /**
     * 숙소 노래방 여부
     */
    @Column(nullable = false)
    @Comment("노래방 여부")
    private boolean karaoke;

    /**
     * 숙소 사우나 여부
     */
    @Column(nullable = false)
    @Comment("사우나 여부")
    private boolean sauna;

    /**
     * 숙소 스포츠 시설 여부
     */
    @Column(nullable = false)
    @Comment("스포츠 시설 여부")
    private boolean sports;

    /**
     * 숙소 세미나실 여부
     */
    @Column(nullable = false)
    @Comment("세미나실 여부")
    private boolean seminar;

    /**
     * 숙소 옵션 Entity Builder
     *
     * @param id            숙소 옵션 식별자
     * @param accommodation 숙소 Entity
     * @param cooking       숙소 객실 내 취사 여부
     * @param parking       숙소 주차 시설 여부
     * @param pickup        숙소 픽업 서비스 여부
     * @param barbecue      숙소 바베큐 장 여부
     * @param fitness       숙소 피트니스 센터 여부
     * @param karaoke       숙소 노래방 여부
     * @param sauna         숙소 사우나 여부
     * @param sports        숙소 스포츠 시설 여부
     * @param seminar       숙소 세미나실 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    private AccommodationOption(
        Long id,
        Accommodation accommodation,
        boolean cooking,
        boolean parking,
        boolean pickup,
        boolean barbecue,
        boolean fitness,
        boolean karaoke,
        boolean sauna,
        boolean sports,
        boolean seminar
    ) {
        this.id = id;
        this.accommodation = accommodation;
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
