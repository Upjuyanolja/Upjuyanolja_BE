package com.backoffice.upjuyanolja.domain.member.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.member.controller.MemberAuthController;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.MemberInfoResponse;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.member.service.MemberAuthService;
import com.backoffice.upjuyanolja.global.security.AuthenticationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = MemberAuthController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class MemberAuthControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private MemberAuthService memberAuthService;

    @MockBean
    private MemberGetService memberGetService;

    @Nested
    @DisplayName("checkEmailDuplicate()는")
    class Context_createComment {

        @Test
        @DisplayName("이메일 중복일 경우 isExists를 true로 응답할 수 있다.")
        void duplicatedEmail_willSuccess() throws Exception {
            // given
            CheckEmailDuplicateResponse checkEmailDuplicateResponse = CheckEmailDuplicateResponse.builder()
                .isExists(true)
                .build();

            given(memberAuthService.checkEmailDuplicate(any(String.class)))
                .willReturn(checkEmailDuplicateResponse);

            // when then
            mockMvc.perform(get("/api/auth/members/email")
                    .queryParam("email", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.isExists").isBoolean())
                .andDo(print());

            verify(memberAuthService, times(1)).checkEmailDuplicate(any(String.class));
        }

        @Test
        @DisplayName("이메일 중복이 아닐 경우 isExists를 false로 응답할 수 있다.")
        void notDuplicatedEmail_willSuccess() throws Exception {
            // given
            CheckEmailDuplicateResponse checkEmailDuplicateResponse = CheckEmailDuplicateResponse.builder()
                .isExists(false)
                .build();

            given(memberAuthService.checkEmailDuplicate(any(String.class)))
                .willReturn(checkEmailDuplicateResponse);

            // when then
            mockMvc.perform(get("/api/auth/members/email")
                    .queryParam("email", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.isExists").isBoolean())
                .andDo(print());

            verify(memberAuthService, times(1)).checkEmailDuplicate(any(String.class));
        }
    }

    @Nested
    @DisplayName("getMember()는")
    class Context_getMember {

        @Test
        @DisplayName("회원 정보를 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                .memberId(1L)
                .email("test@mail.com")
                .name("test")
                .phoneNumber("010-1234-1234")
                .build();

            given(memberGetService.getMember(any(Long.TYPE)))
                .willReturn(memberInfoResponse);

            // when then
            mockMvc.perform(get("/api/auth/members/{memberId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.memberId").isNumber())
                .andExpect(jsonPath("$.data.email").isString())
                .andExpect(jsonPath("$.data.name").isString())
                .andExpect(jsonPath("$.data.phoneNumber").isString())
                .andDo(print());

            verify(memberGetService, times(1)).getMember(any(Long.class));
        }
    }
}