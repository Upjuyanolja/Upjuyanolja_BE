package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Room extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    @Column(nullable = false)
    @Comment("객실 이름")
    private String name;

    @Column(columnDefinition = "TINYINT")
    @Comment("객실 기준 인원")
    private int standard;

    @Column(columnDefinition = "TINYINT")
    @Comment("객실 최대 인원")
    private int capacity;

    @Column(columnDefinition = "TIME")
    @Comment("객실 체크 인 시간")
    private LocalTime checkInTime;

    @Column(columnDefinition = "TIME")
    @Comment("객실 체크 아웃 시간")
    private LocalTime checkOutTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 가격")
    private RoomPrice roomPrice;

    @Column(nullable = false)
    @Comment("객실 개수")
    private int amount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("객실 상태")
    private RoomStatus roomStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 옵션 식별자")
    private RoomOption roomOption;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 이미지 식별자")
    private List<RoomImage> roomImages = new ArrayList<>();

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 재고 식별자")
    private List<RoomStock> roomStocks = new ArrayList<>();

    @OneToMany(mappedBy = "room",
        cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    @Comment("쿠폰 객실 식별자")
    private List<CouponIssuance> couponIssuances = new ArrayList<>();

    @Builder
    public Room(
        Long id,
        Accommodation accommodation,
        String name,
        int standard,
        int capacity,
        int amount,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        RoomPrice roomPrice,
        RoomStatus roomStatus,
        RoomOption roomOption,
        List<RoomImage> roomImages,
        List<RoomStock> roomStocks,
        List<CouponIssuance> couponIssuances
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.name = name;
        this.standard = standard;
        this.capacity = capacity;
        this.amount = amount;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.roomPrice = roomPrice;
        this.roomOption = roomOption;
        this.roomImages = roomImages;
        this.roomStocks = roomStocks;
        this.couponIssuances = couponIssuances;
    }
}
