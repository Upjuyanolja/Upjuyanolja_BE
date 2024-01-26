package com.backoffice.upjuyanolja.domain.point.entity;

import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PointCouponIssuance extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("포인트 쿠폰 발급 내역 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_usage_id")
    @Comment("포인트 사용 내역 식별자")
    private PointUsage pointUsage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_issuance_id")
    @Comment("쿠폰 발급 내역 식별자")
    private CouponIssuance couponIssuance;

    @Builder
    public PointCouponIssuance(
        Long id,
        PointUsage pointUsage,
        CouponIssuance couponIssuance
    ) {
        this.id = id;
        this.pointUsage = pointUsage;
        this.couponIssuance = couponIssuance;
    }

}
