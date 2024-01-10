package com.backoffice.upjuyanolja.domain.member.entity;

import static com.backoffice.upjuyanolja.domain.member.entity.Authority.ROLE_USER;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.common.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 식별자")
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    @Comment("이메일")
    private String email;

    @Column(nullable = false)
    @Comment("암호화된 비밀번호")
    private String password;

    @Column(nullable = false, length = 30)
    @Comment("이름")
    private String name;

    @Column(nullable = false, length = 13)
    @Comment("전화번호")
    private String phone;

    @Column(columnDefinition = "TEXT")
    @Comment("프로필 이미지 URL")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Comment("권한")
    private Authority authority = ROLE_USER;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Point> points = new ArrayList<>();

    @Builder
    public Member(
        Long id,
        String email,
        String password,
        String name,
        String phone,
        String imageUrl,
        Authority authority,
        List<Point> points
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.authority = authority;
        this.points = points;
    }
}
