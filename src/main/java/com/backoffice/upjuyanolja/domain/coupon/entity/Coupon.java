package com.backoffice.upjuyanolja.domain.coupon.entity;

import com.backoffice.upjuyanolja.global.common.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
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

    @Column(nullable = false, name = "coupon_type")
    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 유형")
    private CouponType couponType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("할인 유형")
    private DiscountType discountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 상태")
    private CouponStatus status;

    @Column(nullable = false)
    @Comment("할인 가격(할인 율)")
    private int discount;

    @Column(nullable = false)
    @Comment("쿠폰 노출 만료일")
    private LocalDate endDate;

    @Column(nullable = false)
    @Comment("일일 사용 한도")
    private int dayLimit;

    @Column(nullable = false)
    @Comment("쿠폰 개수(재고)")
    private int count;

    @Builder
    public Coupon(
        Long id,
        CouponType couponType,
        DiscountType discountType,
        CouponStatus status,
        int discount,
        LocalDate endDate,
        int dayLimit,
        int count
    ) {
        this.id = id;
        this.couponType = couponType;
        this.discountType = discountType;
        this.status = status;
        this.discount = discount;
        this.endDate = endDate;
        this.dayLimit = dayLimit;
        this.count = count;
    }
}
