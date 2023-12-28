package com.backoffice.upjuyanolja.domain.member.service;

import com.backoffice.upjuyanolja.domain.member.dto.request.SingUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.exception.MemberEmailDuplicationException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRegisterService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    public SignUpResponse signup(SingUpRequest request) {
        validateDuplicatedEmail(request.email());

        Member member = memberRepository.save(Member.builder()
            .name(request.name())
            .phone(request.phone())
            .email(request.email())
            .password(encoder.encode(request.password()))
            .imageUrl(request.imageUrl())
            .build());

        return SignUpResponse.fromEntity(member);
    }

    public void validateDuplicatedEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberEmailDuplicationException(ErrorCode.DUPLICATED_EMAIL);
        }

    }
}
