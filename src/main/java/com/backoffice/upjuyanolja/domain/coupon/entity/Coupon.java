package com.backoffice.upjuyanolja.domain.coupon.entity;


import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType.Type;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
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
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
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
public class Coupon extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("쿠폰 식별자")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 상태")
    private CouponStatus couponStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("할인 종류")
    private Type type;

    @Column(nullable = false)
    @Comment("할인 가격(할인 율)")
    private int couponPrice;

    @Column(nullable = false)
    @Comment("유효 기간 시작일")
    private LocalDate startDate;

    @Column(nullable = false)
    @Comment("유효 기간 만료일")
    private LocalDate endDate;

    @Column(nullable = false)
    @Comment("일일 사용 한도")
    private int dayLimit;

    @Column(nullable = false)
    @Comment("쿠폰 개수")
    private int count;

    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "coupon",
        cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CouponRoom> couponRooms = new ArrayList<>();

    @Builder
    public Coupon(
        Long id,
        CouponStatus couponStatus,
        Type type,
        int couponPrice,
        LocalDate startDate,
        LocalDate endDate,
        int dayLimit,
        int count,
        List<Reservation> reservations,
        List<CouponRoom> couponRooms
    ) {
        this.id = id;
        this.couponStatus = couponStatus;
        this.type = type;
        this.couponPrice = couponPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayLimit = dayLimit;
        this.count = count;
        this.reservations = reservations;
        this.couponRooms = couponRooms;
    }
}
