package com.backoffice.upjuyanolja.domain.coupon.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "revenue_statistics", indexes = @Index(name = "idx_accommodation_and_revenue_date",
    columnList = "accommodation_id, revenue_date"))
public class RevenueStatistics extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("일자별 매출 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        nullable = false,
        name = "accommodation_id",
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    @Column(nullable = false, name = "revenue_date")
    @Comment("매출 일자")
    private LocalDate revenueDate;

    @Column(nullable = false, name = "coupon_revenue")
    @Comment("쿠폰 사용 매출")
    private long couponRevenue;

    @Column(nullable = false, name = "normal revenue")
    @Comment("쿠폰 미사용 매출")
    private long regularRevenue;

    @Builder
    public RevenueStatistics(
        Long id,
        Accommodation accommodation,
        LocalDate revenueDate,
        long couponRevenue,
        long regularRevenue
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.revenueDate = revenueDate;
        this.couponRevenue = couponRevenue;
        this.regularRevenue = regularRevenue;
    }
}
