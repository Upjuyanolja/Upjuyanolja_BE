package com.backoffice.upjuyanolja.domain.accommodation.entity;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Accommodation extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("숙소 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("숙소 이름")
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("숙소 설명")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("숙소 대표 이미지 URL")
    private String thumbnail;

    @Column(nullable = false)
    @Comment("주소")
    private String address;

    @Column(nullable = false)
    @Comment("상세 주소")
    private String detailAddress;

    @Column(nullable = false)
    @Comment("우편번호")
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Comment("숙소 카테고리")
    private Category category;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @Builder
    public Accommodation(
        Long id,
        String name,
        String address,
        String detailAddress,
        String zipCode,
        Category category,
        String description,
        String thumbnail,
        List<Room> rooms
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.category = category;
        this.description = description;
        this.thumbnail = thumbnail;
        this.rooms = rooms;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
