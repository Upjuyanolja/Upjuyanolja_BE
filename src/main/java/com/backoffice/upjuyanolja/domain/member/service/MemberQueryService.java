package com.backoffice.upjuyanolja.domain.member.service;

import com.backoffice.upjuyanolja.domain.member.dto.response.MemberInfoResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 조회 Service Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class MemberQueryService {

    /**
     * 회원 Repository Interface
     */
    private final MemberRepository memberRepository;

    /**
     * 회원 정보 조회 메서드
     *
     * @param memberId 회원 식별자
     * @return 회원 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public MemberInfoResponse getMember(long memberId) {
        return MemberInfoResponse.of(getMemberById(memberId));
    }

    /**
     * 회원 조회 메서드
     *
     * @param memberId 회원 식별자
     * @return 회원 Entity
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우 에러 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public Member getMemberById(long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
