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
public class PointRefunds extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("포인트 환불 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("환불 일시")
    private LocalDateTime refundDate;

    @Column(nullable = false)
    @Comment("포인트 식별자")
    private Long pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_charges_id")
    @Comment("포인트 충전 식별자")
    private PointCharges pointCharges;

    @Builder
    public PointRefunds(
        Long id,
        LocalDateTime refundDate,
        Long pointId,
        PointCharges pointCharges
    ) {
        this.id = id;
        this.refundDate = refundDate;
        this.pointId = pointId;
        this.pointCharges = pointCharges;
    }

}
