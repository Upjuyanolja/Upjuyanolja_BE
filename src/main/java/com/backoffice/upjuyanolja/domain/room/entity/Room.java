package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase.RoomUpdate;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import com.backoffice.upjuyanolja.global.util.DateTimeParser;
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
import java.time.LocalDateTime;
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
    private RoomPrice price;

    @Column(nullable = false)
    @Comment("객실 개수")
    private int amount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("객실 상태")
    private RoomStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 옵션 식별자")
    private RoomOption option;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 이미지 식별자")
    private List<RoomImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 재고 식별자")
    private List<RoomStock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "room",
        cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    @Comment("쿠폰 객실 식별자")
    private List<CouponIssuance> couponIssuances = new ArrayList<>();

    @Builder
    public Room(
        Long id,
        Accommodation accommodation,
        String name,
        int defaultCapacity,
        int maxCapacity,
        int amount,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        RoomPrice price,
        RoomStatus status,
        RoomOption option,
        List<RoomImage> images,
        List<RoomStock> stocks,
        List<CouponIssuance> couponIssuances
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.name = name;
        this.defaultCapacity = defaultCapacity;
        this.maxCapacity = maxCapacity;
        this.amount = amount;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.price = price;
        this.status = status;
        this.option = option;
        this.images = images;
        this.stocks = stocks;
        this.couponIssuances = couponIssuances;
    }

    public void updateRoom(RoomUpdate request) {
        this.name = request.name();
        this.status = RoomStatus.valueOf(request.status());
        this.price.updateRoomPrice(request.price());
        this.defaultCapacity = request.defaultCapacity();
        this.maxCapacity = request.maxCapacity();
        this.checkInTime = DateTimeParser.timeParser(request.checkInTime());
        this.checkOutTime = DateTimeParser.timeParser(request.checkOutTime());
        this.amount = request.amount();
        this.option.updateRoomOption(request.option());
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }
}
