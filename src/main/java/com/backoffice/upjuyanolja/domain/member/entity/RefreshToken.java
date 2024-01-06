package com.backoffice.upjuyanolja.domain.member.entity;

import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshToken extends BaseTime {

    @Id
    @Comment("Refresh Token 식별자(회원 식별자")
    private String Id;

    @Comment("Refresh Token")
    private String token;

    @Builder
    public RefreshToken(String id, String token) {
        Id = id;
        this.token = token;
    }

    public RefreshToken updateRefreshToken(String token){
        this.token = token;
        return this;
    }
}
