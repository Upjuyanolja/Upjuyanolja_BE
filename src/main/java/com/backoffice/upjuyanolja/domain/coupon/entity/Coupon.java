package com.backoffice.upjuyanolja.domain.coupon.entity;

import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.global.common.BaseTime;
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
    @Enumerated(value = EnumType.STRING)
    @Comment("쿠폰 종류")
    private CouponType type;
    @Column(nullable = false)
    @Comment("할인가/율")
    private int discount;
    @Column(nullable = false)
    @Comment("시작일")
    private LocalDate startDate;
    @Column(nullable = false)
    @Comment("시작일")
    private LocalDate endDate;
    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @Builder
    public Coupon(
        Long id,
        CouponType type,
        int discount,
        LocalDate startDate,
        LocalDate endDate,
        List<Reservation> reservations
    ) {
        this.id = id;
        this.type = type;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservations = reservations;
    }
}
