package com.backoffice.upjuyanolja.global.security;

import static org.springframework.util.StringUtils.hasText;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.entity.Owner;
import com.backoffice.upjuyanolja.domain.member.exception.AuthorizeInfoNotFoundException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.member.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtil {

    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;

    public Long getCurrentMemberId() {

        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication == null || !hasText(authentication.getName()) || authentication.getName()
            .equals("anonymousUser")) {
            throw new AuthorizeInfoNotFoundException();
        }
        log.info("getName is {}", authentication.getName());
        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(
            MemberNotFoundException::new
        );
        return member.getId();
    }

    public Long getCurrentOwnerId() {

        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();

        if (authentication == null || !hasText(authentication.getName()) || authentication.getName()
            .equals("anonymousUser")) {
            throw new AuthorizeInfoNotFoundException();
        }
        log.info("getName is {}", authentication.getName());
        Owner owner = ownerRepository.findByEmail(authentication.getName()).orElseThrow(
            MemberNotFoundException::new
        );
        return owner.getId();
    }
}
