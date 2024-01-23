package com.backoffice.upjuyanolja.global.util;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.backoffice.upjuyanolja.global.config.RestDocsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@Disabled
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@Import(RestDocsConfig.class)
public abstract class RestDocsSupport {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RestDocumentationResultHandler restDoc;

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(documentationConfiguration(
                restDocumentationContextProvider).operationPreprocessors()
                .withRequestDefaults(modifyHeaders()
                    .remove("Vary")
                    .remove("X-Content-Type-Options")
                    .remove("X-XSS-Protection")
                    .remove("Cache-Control")
                    .remove("Pragma")
                    .remove("Expires")
                    .remove("X-Frame-Options")
                    .remove("Content-Length")
                    .remove("Host"))
                .withResponseDefaults(prettyPrint()))
            .alwaysDo(print())
            .alwaysDo(restDoc)
            .apply(springSecurity())
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    protected FieldDescriptor[] failResponseCommon() {
        return new FieldDescriptor[]{
            fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")};
    }
}
