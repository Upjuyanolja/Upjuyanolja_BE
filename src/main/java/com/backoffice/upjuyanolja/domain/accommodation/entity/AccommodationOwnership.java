package com.backoffice.upjuyanolja.domain.accommodation.entity;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
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

/**
 * 숙소 소유권 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AccommodationOwnership {

    /**
     * 숙소 소유권 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("숙소 소유권 식별자")
    private Long id;

    /**
     * 숙소 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    /**
     * 회원 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @Comment("회원 식별자")
    private Member member;

    /**
     * 숙소 소유권 Entity Builder
     *
     * @param id            숙소 소유권 식별자
     * @param accommodation 숙소 Entity
     * @param member        회원 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    private AccommodationOwnership(
        Long id,
        Accommodation accommodation,
        Member member
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.member = member;
    }
}
