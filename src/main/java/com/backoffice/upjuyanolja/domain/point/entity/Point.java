package com.backoffice.upjuyanolja.domain.point.entity;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
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
public class Point extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("포인트 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("보유 포인트")
    private long totalPointBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Comment("회원 식별자")
    private Member member;

    @Builder
    public Point(
        Long id,
        long totalPointBalance,
        Member member
    ) {
        this.id = id;
        this.totalPointBalance = totalPointBalance;
        this.member = member;
    }

    public void updatePointBalance(long totalPointBalance) {
        this.totalPointBalance = totalPointBalance;
    }

}
