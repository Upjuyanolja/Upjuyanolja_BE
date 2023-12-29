package com.backoffice.upjuyanolja.domain.member.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.service.MemberRegisterService;
import com.backoffice.upjuyanolja.global.util.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;

public class MemberControllerDocsTest extends RestDocsSupport {

    @MockBean
    private MemberRegisterService memberRegisterService;

    @Test
    @DisplayName("checkEmailDuplicate()는 이메일 중복 검사를 할 수 있다.")
    void checkEmailDuplicate() throws Exception {
        // given
        CheckEmailDuplicateResponse checkEmailDuplicateResponse = CheckEmailDuplicateResponse.builder()
            .isExists(true)
            .build();

        given(memberRegisterService.checkEmailDuplicate(any(String.class)))
            .willReturn(checkEmailDuplicateResponse);

        // when then
        mockMvc.perform(get("/api/members/email")
                .queryParam("email", "test@mail.com"))
            .andDo(restDoc.document(
                queryParameters(
                    parameterWithName("email").description("이메일")
                ),
                responseFields(successResponseCommon()).and(
                    fieldWithPath("data.isExists").type(JsonFieldType.BOOLEAN).description("이메일 중복 여부")
                )
            ));
    }
}
