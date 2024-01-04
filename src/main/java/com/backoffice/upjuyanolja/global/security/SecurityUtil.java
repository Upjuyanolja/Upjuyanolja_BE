package com.backoffice.upjuyanolja.global.security;

import com.backoffice.upjuyanolja.domain.member.exception.AuthorizeInfoNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getCurrentMemberId() {

        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AuthorizeInfoNotFoundException();
        }
        return Long.parseLong(authentication.getName());
    }

    public Long getNullableCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName()
            .equals("anonymousUser")) {
            return null;
        }

        return Long.parseLong(authentication.getName());
    }
}
