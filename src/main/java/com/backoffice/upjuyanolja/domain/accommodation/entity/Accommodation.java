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
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("숙소 위치")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Comment("숙소 카테고리")
    private Category category;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("숙소 설명")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("숙소 대표 이미지 URL")
    private String thumbnail;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("숙소 옵션 식별자")
    private AccommodationOption option;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccommodationImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @Builder
    public Accommodation(
        Long id,
        String name,
        Address address,
        Category category,
        String description,
        String thumbnail,
        AccommodationOption option,
        List<AccommodationImage> images,
        List<Room> rooms
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.category = category;
        this.description = description;
        this.thumbnail = thumbnail;
        this.option = option;
        this.images = images;
        this.rooms = rooms;
    }
}
