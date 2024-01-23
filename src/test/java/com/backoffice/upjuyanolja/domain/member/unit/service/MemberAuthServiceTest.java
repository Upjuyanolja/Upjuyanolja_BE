package com.backoffice.upjuyanolja.domain.member.unit.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backoffice.upjuyanolja.domain.member.dto.request.EmailRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.member.service.MemberAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class MemberAuthServiceTest {

    @InjectMocks
    private MemberAuthService memberAuthService;

    @Mock
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("checkEmailDuplicate()는")
    class Context_checkEmailDuplicate {

        @Test
        @DisplayName("이메일이 존재하면 isExists에 true를 담아 반환한다.")
        void isExists_willSuccess() {
            // given
            EmailRequest emailRequest = EmailRequest.builder()
                .email("test@mail.com")
                .build();

            given(memberRepository.existsByEmail(any(String.class))).willReturn(true);

            // when
            CheckEmailDuplicateResponse result = memberAuthService
                .checkEmailDuplicate(emailRequest);

            // then
            assertTrue(result.isExists());

            verify(memberRepository, times(1)).existsByEmail(any(String.class));
        }

        @Test
        @DisplayName("이메일이 존재하면 isExists에 false를 담아 반환한다.")
        void isNotExists_willSuccess() {
            // given
            EmailRequest emailRequest = EmailRequest.builder()
                .email("test@mail.com")
                .build();

            given(memberRepository.existsByEmail(any(String.class))).willReturn(false);

            // when
            CheckEmailDuplicateResponse result = memberAuthService.checkEmailDuplicate(
                emailRequest);

            // then
            assertFalse(result.isExists());

            verify(memberRepository, times(1)).existsByEmail(any(String.class));
        }
    }
}
