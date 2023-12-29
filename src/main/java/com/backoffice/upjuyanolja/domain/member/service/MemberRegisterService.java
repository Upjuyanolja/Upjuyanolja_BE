package com.backoffice.upjuyanolja.domain.member.service;

import com.backoffice.upjuyanolja.domain.member.dto.request.SignUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.exception.IncorrectPasswordException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberEmailDuplicationException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberRegisterService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public SignUpResponse signup(SignUpRequest request) {
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

    @Transactional
    public String signin(String email, String password) {
        //회원가입 여부 체크
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new MemberNotFoundException()
        );

        //비밀번호 체크
        if (!member.getPassword().equals(password)) {
            throw new IncorrectPasswordException();
        }

        //토큰 발급
        //TODO implmentation

        return "";
    }

    public void validateDuplicatedEmail(String email) {
        if (isDuplicatedEmail(email)) {
            throw new MemberEmailDuplicationException();
        }
    }

    public CheckEmailDuplicateResponse checkEmailDuplicate(String email) {
        return CheckEmailDuplicateResponse.builder()
            .isExists(isDuplicatedEmail(email))
            .build();
    }

    public boolean isDuplicatedEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
