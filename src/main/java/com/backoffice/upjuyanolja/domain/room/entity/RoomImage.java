package com.backoffice.upjuyanolja.domain.room.entity;

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

/**
 * 객실 이미지 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoomImage {

    /**
     * 객실 이미지 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 이미지 식별자")
    private Long id;

    /**
     * 객실 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room_id")
    @Comment("객실 식별자")
    private Room room;

    /**
     * 객실 이미지 URL
     */
    @Column(nullable = false)
    @Comment("객실 이미지 URL")
    private String url;

    /**
     * 객실 이미지 Entity Builder
     *
     * @param id   객실 이미지 식별자
     * @param room 객실 이미지
     * @param url  객실 이미지 URL
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    private RoomImage(
        Long id,
        Room room,
        String url
    ) {
        this.id = id;
        this.room = room;
        this.url = url;
    }
}
