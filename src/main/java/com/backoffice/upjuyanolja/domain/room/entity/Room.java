package com.backoffice.upjuyanolja.domain.room.entity;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
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
import java.time.LocalTime;
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
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    @Comment("숙소 식별자")
    private Accommodation accommodation;

    @Column(nullable = false)
    @Comment("객실 이름")
    private String name;

    @Column(columnDefinition = "TINYINT")
    @Comment("객실 기준인원")
    private int standard;

    @Column(columnDefinition = "TINYINT")
    @Comment("객실 최대인원")
    private int capacity;

    @Column(columnDefinition = "TIME")
    @Comment("객실 체크인 시간")
    private LocalTime checkIn;

    @Column(columnDefinition = "TIME")
    @Comment("객실 체크아웃 시간")
    private LocalTime checkOut;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 가격")
    private RoomPrice price;

    @Column(nullable = false)
    @Comment("객실 개수")
    private int amount;

    @Column(nullable = false)
    @Comment("객실 상태")
    private RoomStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 옵션 식별자")
    private RoomOption option;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("객실 이미지 식별자")
    private List<RoomImage> images = new ArrayList<>();

    @Builder
    public Room(
        Long id,
        Accommodation accommodation,
        String name,
        int standard,
        int capacity,
        LocalTime checkIn,
        LocalTime checkOut,
        RoomPrice price,
        int amount,
        RoomStatus status,
        RoomOption option,
        List<RoomImage> images
    ) {
        this.id = id;
        this.accommodation = accommodation;
        this.name = name;
        this.standard = standard;
        this.capacity = capacity;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.price = price;
        this.amount = amount;
        this.status = status;
        this.option = option;
        this.images = images;
    }
}
