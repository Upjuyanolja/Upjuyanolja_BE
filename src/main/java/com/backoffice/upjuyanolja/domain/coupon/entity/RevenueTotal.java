package com.backoffice.upjuyanolja.domain.coupon.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "revenue_total", indexes = @Index(name = "idx_accommodation_id",
    columnList = "accommodation_id"))
public class RevenueTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("일주일 간 매출 합계 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        nullable = false,
        name = "accommodation_id",
        unique = true,
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    @Column(nullable = false, name = "coupon_revenue")
    @Comment("쿠폰 사용 매출 합계")
    private long couponTotal;

    @Column(nullable = false, name = "normal revenue")
    @Comment("쿠폰 미사용 매출 합계")
    private long regularTotal;

    @Column(name = "growth_rate")
    @Comment("매출 상승 비율")
    private double growthRate;

    @Builder
    public RevenueTotal(
        Long id,
        Accommodation accommodation,
        long couponTotal,
        long regularTotal,
        double growthRate
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.couponTotal = couponTotal;
        this.regularTotal = regularTotal;
        this.growthRate = growthRate;
    }

    public RevenueTotal updateGrowthRate(double growthRate) {
        this.growthRate = growthRate;
        return this;
    }

}
