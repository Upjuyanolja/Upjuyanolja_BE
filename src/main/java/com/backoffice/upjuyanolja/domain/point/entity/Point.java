package com.backoffice.upjuyanolja.domain.point.entity;

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
import org.hibernate.validator.constraints.Range;

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
    private long pointBalance;

    @Column(nullable = false)
    @Comment("기준 달")
    @Range(min = 1, max = 12, message = "기준 달은 1에서 12 사이 여야 합니다")
    private int standardMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Comment("회원 식별자")
    private Member member;


    @Builder
    public Point(
        Long id,
        int pointBalance,
        Member member
    ) {
        this.id = id;
        this.pointBalance = pointBalance;
        this.member = member;
    }

}
