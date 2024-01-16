package com.backoffice.upjuyanolja.global.security;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.exception.AuthorizeInfoNotFoundException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final MemberRepository memberRepository;

    public Long getCurrentMemberId() {

        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AuthorizeInfoNotFoundException();
        }
        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(
            MemberNotFoundException::new
        );
        return member.getId();
    }
}
