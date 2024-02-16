package com.backoffice.upjuyanolja.domain.member.dto.response;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import lombok.Builder;

/**
 * 회원 정보 응답 DTO Record
 *
 * @param memberId    회원 식별자
 * @param email       회원 이메일
 * @param name        회원 이름
 * @param phoneNumber 회원 전화번호
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record MemberInfoResponse(
    long memberId,
    String email,
    String name,
    String phoneNumber
) {

    /**
     * 회원 Entity로 회원 정보 응답 DTO을 생성하는 정적 팩토리 메서드
     *
     * @param member 회원 Entity
     * @return 회원 정보 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .phoneNumber(member.getPhone())
            .build();
    }
}
