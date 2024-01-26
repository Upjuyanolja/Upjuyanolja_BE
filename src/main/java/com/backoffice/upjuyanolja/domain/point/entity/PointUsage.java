package com.backoffice.upjuyanolja.domain.point.entity;

import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class PointUsage extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("포인트 사용 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("주문 일시")
    private LocalDateTime orderDate;

    @Column(nullable = false)
    @Comment("주문 금액")
    private long orderPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private Point point;

    @Builder
    public PointUsage(
        Long id,
        LocalDateTime orderDate,
        long orderPrice,
        Point point
    ) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.point = point;
    }
}
