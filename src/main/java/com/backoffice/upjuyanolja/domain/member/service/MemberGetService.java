package com.backoffice.upjuyanolja.domain.member.service;

import com.backoffice.upjuyanolja.domain.member.dto.response.GetMemberResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberGetService {

    private final MemberRepository memberRepository;

    public GetMemberResponse getMember(long memberId) {
        return GetMemberResponse.of(getMemberById(memberId));
    }

    public Member getMemberById(long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }
}
