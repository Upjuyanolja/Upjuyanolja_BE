package com.backoffice.upjuyanolja.domain.member.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.member.controller.MemberController;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.service.MemberRegisterService;
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

@WebMvcTest(value = MemberController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class MemberControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private MemberRegisterService memberRegisterService;

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

            given(memberRegisterService.checkEmailDuplicate(any(String.class)))
                .willReturn(checkEmailDuplicateResponse);

            // when then
            mockMvc.perform(get("/api/members/email")
                    .queryParam("email", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.isExists").isBoolean())
                .andDo(print());

            verify(memberRegisterService, times(1)).checkEmailDuplicate(any(String.class));
        }

        @Test
        @DisplayName("이메일 중복이 아닐 경우 isExists를 false로 응답할 수 있다.")
        void notDuplicatedEmail_willSuccess() throws Exception {
            // given
            CheckEmailDuplicateResponse checkEmailDuplicateResponse = CheckEmailDuplicateResponse.builder()
                .isExists(false)
                .build();

            given(memberRegisterService.checkEmailDuplicate(any(String.class)))
                .willReturn(checkEmailDuplicateResponse);

            // when then
            mockMvc.perform(get("/api/members/email")
                    .queryParam("email", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.isExists").isBoolean())
                .andDo(print());

            verify(memberRegisterService, times(1)).checkEmailDuplicate(any(String.class));
        }
    }
}
