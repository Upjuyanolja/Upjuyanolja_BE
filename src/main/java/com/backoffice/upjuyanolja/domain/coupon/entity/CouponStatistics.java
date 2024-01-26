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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponStatistics extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("쿠폰 사용량 통계 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        nullable = false,
        name = "accommodation_id",
        unique = true,
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Accommodation accommodation;

    @Setter
    @Column(nullable = false)
    @Comment("발행 쿠폰")
    private long total;

    @Setter
    @Column(nullable = false)
    @Comment("사용 완료 쿠폰")
    private long used;

    @Setter
    @Column(nullable = false)
    @Comment("현재 보유 쿠폰")
    private long stock;

    @Builder
    public CouponStatistics(
        Long id,
        Accommodation accommodation,
        long total,
        long used,
        long stock
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.total = total;
        this.used = used;
        this.stock = stock;
    }
}
