package com.backoffice.upjuyanolja.domain.accommodation.entity;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 숙소 카테고리 Entity Class
 *
 * @author HyunA (vikim1210@naver.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

    /**
     * 숙소 카테고리 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("카테고리 식별자")
    private Long id;

    /**
     * 숙소 카테고리 이름
     */
    @Column(nullable = false)
    @Comment("카테고리 이름")
    private String name;

    /**
     * 숙소 상위 카테고리 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category parent;

    /**
     * 숙소 카테고리 Entity Builder
     *
     * @param id     숙소 카테고리 식별자
     * @param name   숙소 카테고리 이름
     * @param parent 숙소 상위 카테고리 Entity
     * @author HyunA (vikim1210@naver.com)
     */
    @Builder
    private Category(
        Long id,
        String name,
        Category parent
    ) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }
}
