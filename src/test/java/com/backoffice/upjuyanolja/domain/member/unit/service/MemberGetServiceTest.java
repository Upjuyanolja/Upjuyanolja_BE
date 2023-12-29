package com.backoffice.upjuyanolja.domain.member.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backoffice.upjuyanolja.domain.member.dto.response.GetMemberResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import java.util.Optional;
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
public class MemberGetServiceTest {

    @InjectMocks
    private MemberGetService memberGetService;

    @Mock
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("getMember()는")
    class Context_getMember {

        @Test
        @DisplayName("회원 정보를 조회할 수 있다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .email("test@mail.com")
                .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
                .name("test")
                .phone("010-1234-1234")
                .imageUrl(
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
                .authority(Authority.ROLE_USER)
                .build();
            given(memberRepository.findById(any(Long.TYPE))).willReturn(Optional.of(member));

            // when
            GetMemberResponse result = memberGetService.getMember(1L);

            // then
            assertThat(result.memberId()).isEqualTo(1L);
            assertThat(result.email()).isEqualTo("test@mail.com");
            assertThat(result.name()).isEqualTo("test");
            assertThat(result.phoneNumber()).isEqualTo("010-1234-1234");

            verify(memberRepository, times(1)).findById(any(Long.TYPE));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면, 익셉션을 던진다.")
        void memberNotFound_willFail() {
            // given
            given(memberRepository.findById(any(Long.TYPE))).willReturn(Optional.empty());

            // when
            Throwable exception = assertThrows(MemberNotFoundException.class, () -> {
                memberGetService.getMember(1L);
            });

            // then
            assertEquals(exception.getMessage(), "회원 정보를 찾을 수 없습니다.");

            verify(memberRepository, times(1)).findById(any(Long.TYPE));
        }
    }
}
