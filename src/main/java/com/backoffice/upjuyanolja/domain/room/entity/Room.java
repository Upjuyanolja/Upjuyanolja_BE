package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponRoom;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    @Column(nullable = false)
    @Comment("객실 코드")
    private long code;

    @Column(nullable = false)
    @Comment("객실 이름")
    private String name;

    @Column(nullable = false)
    @Comment("객실 갯수")
    private int count;

    @Column(columnDefinition = "TINYINT")
    @Comment("객실 기준 인원")
    private int defaultCapacity;

    @Column(columnDefinition = "TINYINT")
    @Comment("객실 최대 인원")
    private int maxCapacity;

    @Column(columnDefinition = "TIME")
    @Comment("객실 체크 인 시간")
    private LocalTime checkInTime;

    @Column(columnDefinition = "TIME")
    @Comment("객실 체크 아웃 시간")
    private LocalTime checkOutTime;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 가격")
    private RoomPrice roomPrice;

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
    @Comment("쿠폰-객실 식별자")
    private List<CouponRoom> couponRooms = new ArrayList<>();

    @Builder
    public Room(
        Long id,
        Accommodation accommodation,
        long code,
        int count,
        String name,
        int defaultCapacity,
        int maxCapacity,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        RoomPrice roomPrice,
        RoomOption roomOption,
        List<RoomImage> roomImages,
        List<CouponRoom> couponRooms
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.code = code;
        this.count = count;
        this.name = name;
        this.defaultCapacity = defaultCapacity;
        this.maxCapacity = maxCapacity;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.roomPrice = roomPrice;
        this.roomOption = roomOption;
        this.roomImages = roomImages;
        this.couponRooms = couponRooms;
    }
}
