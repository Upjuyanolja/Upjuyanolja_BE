package com.backoffice.upjuyanolja.domain.member.entity;

import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 회원 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 * @author chadongmin (cdm2883@naver.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTime {

    /**
     * 회원 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 식별자")
    private Long id;

    /**
     * 회원 이메일
     */
    @Column(unique = true, nullable = false, length = 30)
    @Comment("이메일")
    private String email;

    /**
     * 회원의 암호화된 비밀번호
     */
    @Column(nullable = false)
    @Comment("암호화된 비밀번호")
    private String password;

    /**
     * 회원 이름
     */
    @Column(nullable = false, length = 30)
    @Comment("이름")
    private String name;

    /**
     * 회원 전화번호
     */
    @Column(nullable = false, length = 13)
    @Comment("전화번호")
    private String phone;

    /**
     * 회원 프로필 이미지 URL
     */
    @Column(columnDefinition = "TEXT")
    @Comment("프로필 이미지 URL")
    private String imageUrl;

    /**
     * 회원 권한
     */
    @Enumerated(EnumType.STRING)
    @Comment("권한")
    private Authority authority;

    /**
     * 회원 Entity Builder
     *
     * @param id        회원 식별자
     * @param email     회원 이메일
     * @param password  회원의 암호화된 비밀번호
     * @param name      회원 이름
     * @param phone     회원 전화번호
     * @param imageUrl  회원 프로필 이미지 URL
     * @param authority 회원 권한
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Builder
    public Member(
        Long id,
        String email,
        String password,
        String name,
        String phone,
        String imageUrl,
        Authority authority
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.authority = authority;
    }
}
