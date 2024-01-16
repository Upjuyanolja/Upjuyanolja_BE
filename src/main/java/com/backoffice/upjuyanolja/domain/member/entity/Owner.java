package com.backoffice.upjuyanolja.domain.member.entity;

import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
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
public class Owner extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 식별자")
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    @Comment("이메일")
    private String email;

    @Column(nullable = false, length = 30)
    @Comment("이름")
    private String name;

    @Column(nullable = false, length = 13)
    @Comment("전화번호")
    private String phone;

    @Builder
    public Owner(Long id,
        String email,
        String name,
        String phone
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
}
