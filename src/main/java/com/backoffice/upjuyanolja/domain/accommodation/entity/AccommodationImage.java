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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AccommodationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("숙소 이미지 식별자")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "accommodation_id")
    @Comment("숙소 식별자")
    private Accommodation accommodation;
    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("이미지 URL")
    private String url;

    @Builder
    public AccommodationImage(
        Long id,
        Accommodation accommodation,
        String url
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.url = url;
    }
}
