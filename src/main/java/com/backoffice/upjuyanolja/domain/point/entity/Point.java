package com.backoffice.upjuyanolja.domain.point.entity;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.POINT_INSUFFICIENT;

import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientPointsException;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Point extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("포인트 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("보유 포인트")
    private int pointBalance;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("포인트 유형")
    private PointType pointType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Comment("회원 식별자")
    private Member member;

    public void decreasePointBalance(int usedPoints) {
        if (pointBalance - usedPoints < 0) {
            throw new InsufficientPointsException();
        }
        pointBalance -= usedPoints;
    }

    @Builder
    public Point(
        Long id,
        int pointBalance,
        PointType pointType,
        Member member
    ) {
        this.id = id;
        this.pointBalance = pointBalance;
        this.pointType = pointType;
        this.member = member;
    }

}
