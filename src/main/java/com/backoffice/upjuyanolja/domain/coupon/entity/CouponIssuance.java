package com.backoffice.upjuyanolja.domain.coupon.entity;

import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon_issuance", indexes = @Index(name = "idx_room_and_coupon_id",
    columnList = "room_id, coupon_id"))
public class CouponIssuance extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("쿠폰 발급 내역 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "coupon_id")
    @Comment("쿠폰 식별자")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room_id")
    @Comment("객실 식별자")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "point_usage_id")
    @Comment("객실 식별자")
    private PointUsage pointUsage;

    @Column(nullable = false)
    @Comment("발급 수량")
    private int quantity;

    @Column(nullable = false)
    @Comment("쿠폰 구매 금액")
    private int amount;

    @Builder
    public CouponIssuance(
        Long id,
        Coupon coupon,
        Room room,
        PointUsage pointUsage,
        int quantity,
        int amount
    ) {
        this.id = id;
        this.coupon = coupon;
        this.room = room;
        this.pointUsage = pointUsage;
        this.quantity = quantity;
        this.amount = amount;
    }

}
