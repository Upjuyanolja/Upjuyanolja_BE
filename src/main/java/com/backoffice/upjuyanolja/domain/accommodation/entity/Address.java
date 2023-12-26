package com.backoffice.upjuyanolja.domain.accommodation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주소 식별자")
    private Long id;
    @Column(nullable = false)
    @Comment("주소")
    private String address;
    @Column(nullable = false)
    @Comment("상세 주소")
    private String detailAddress;
    @Column(nullable = false)
    @Comment("X좌표")
    private double mapX;
    @Column(nullable = false)
    @Comment("Y좌표")
    private double mapY;

    @Builder
    public Address(
        Long id,
        String address,
        String detailAddress,
        double mapX,
        double mapY
    ) {
        this.id = id;
        this.address = address;
        this.detailAddress = detailAddress;
        this.mapX = mapX;
        this.mapY = mapY;
    }
}
