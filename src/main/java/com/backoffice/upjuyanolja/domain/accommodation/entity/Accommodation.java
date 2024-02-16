package com.backoffice.upjuyanolja.domain.accommodation.entity;

import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 숙소 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Accommodation extends BaseTime {

    /**
     * 숙소 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("숙소 식별자")
    private Long id;

    /**
     * 숙소 이름
     */
    @Column(nullable = false)
    @Comment("숙소 이름")
    private String name;

    /**
     * 숙소 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Comment("숙소 카테고리")
    private Category category;

    /**
     * 숙소 설명
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("숙소 설명")
    private String description;

    /**
     * 숙소 대표 이미지 URL
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("숙소 대표 이미지 URL")
    private String thumbnail;

    /**
     * 숙소 주소
     */
    @Column(nullable = false)
    @Comment("주소")
    private String address;

    /**
     * 숙소 상세 주소
     */
    @Column(nullable = false)
    @Comment("상세 주소")
    private String detailAddress;

    /**
     * 숙소 우편 번호
     */
    @Column(nullable = false)
    @Comment("우편 번호")
    private String zipCode;

    /**
     * 숙소 Entity Builder
     *
     * @param id            숙소 식별자
     * @param name          숙소 이름
     * @param category      숙소 카테고리
     * @param description   숙소 설명
     * @param thumbnail     숙소 대표 이미지
     * @param address       숙소 주소
     * @param detailAddress 숙소 상세 주소
     * @param zipCode       숙소 우편 번호
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    private Accommodation(
        Long id,
        String name,
        Category category,
        String description,
        String thumbnail,
        String address,
        String detailAddress,
        String zipCode
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.thumbnail = thumbnail;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }

    /**
     * 논리적 동일성을 비교하는 equals 메서드 재정의
     *
     * @param o 비교하고자 하는 객체
     * @return 일치 여부
     * @author Jisang Lee (matrixpower1004@gmail.com)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Accommodation that = (Accommodation) o;
        return Objects.equals(getId(), that.getId());
    }

    /**
     * equals 비교를 통해 같은 객체의 경우 항상 같은 해시 코드 값을 가지도록 hashCode 메서드 재정의
     *
     * @return 객체 해시 코드 값
     * @author Jisang Lee (matrixpower1004@gmail.com)
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
