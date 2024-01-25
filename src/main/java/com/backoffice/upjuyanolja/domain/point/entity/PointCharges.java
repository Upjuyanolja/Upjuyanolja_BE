package com.backoffice.upjuyanolja.domain.point.entity;

import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PointCharges extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("포인트 충전 식별자")
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("포인트 유형")
    private PointStatus pointStatus;

    @Column(nullable = false)
    @Comment("포인트 결제 키")
    private String paymentKey;

    @Column(nullable = false)
    @Comment("주문 이름")
    private String orderName;

    @Column(nullable = false)
    @Comment("충전 포인트")
    private long chargePoint;

    @Column(nullable = false)
    @Comment("충전 일시")
    private LocalDateTime chargeDate;

    @Column(nullable = false)
    @Comment("마감 일시")
    private LocalDateTime endDate;

    @Column(nullable = false)
    @Comment("환불 가능 여부")
    private boolean refundable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    @Comment("포인트 식별자")
    private Point point;


    @Builder
    public PointCharges(
        Long id,
        PointStatus pointStatus,
        String paymentKey,
        String orderName,
        long chargePoint,
        LocalDateTime chargeDate,
        LocalDateTime endDate,
        boolean refundable,
        Point point
    ) {
        this.id = id;
        this.pointStatus = pointStatus;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.chargePoint = chargePoint;
        this.chargeDate = chargeDate;
        this.endDate = endDate;
        this.refundable = refundable;
        this.point = point;
    }

}
