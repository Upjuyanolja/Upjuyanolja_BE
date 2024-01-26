package com.backoffice.upjuyanolja.domain.accommodation.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageUrlResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.ImageService;
import com.backoffice.upjuyanolja.global.util.RestDocsSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class ImageControllerDocsTest extends RestDocsSupport {

    @MockBean
    private ImageService imageService;

    @Test
    @DisplayName("이미지를 저장할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void saveImages() throws Exception {
        // given
        URL resource = getClass().getResource("/images/image_sample.jpg");

        List<ImageUrlResponse> urls = new ArrayList<>();
        urls.add(ImageUrlResponse.builder()
            .url("https://aws/coupons/images/001")
            .build());
        urls.add(ImageUrlResponse.builder()
            .url("https://aws/coupons/images/002")
            .build());
        urls.add(ImageUrlResponse.builder()
            .url("https://aws/coupons/images/003")
            .build());
        urls.add(ImageUrlResponse.builder()
            .url("https://aws/coupons/images/004")
            .build());
        ImageResponse imageResponse = ImageResponse.builder()
            .urls(urls)
            .build();

        given(imageService.saveImages(any(List.class)))
            .willReturn(imageResponse);

        // when then
        mockMvc.perform(multipart("/backoffice-api/images")
                .file(new MockMultipartFile(
                    "image1",
                    "image_sample.jpg",
                    "images/png",
                    resource.openStream().readAllBytes()
                ))
                .file(new MockMultipartFile(
                    "image2",
                    "image_sample.jpg",
                    "images/png",
                    resource.openStream().readAllBytes()
                ))
                .file(new MockMultipartFile(
                    "image3",
                    "image_sample.jpg",
                    "images/png",
                    resource.openStream().readAllBytes()
                ))
                .file(new MockMultipartFile(
                    "image4",
                    "image_sample.jpg",
                    "images/png",
                    resource.openStream().readAllBytes()
                ))
                .file(new MockMultipartFile(
                    "image5",
                    new byte[0]
                )))
            .andDo(restDoc.document(
                requestParts(
                    partWithName("image1").description("저장할 이미지 1"),
                    partWithName("image2").description("저장할 이미지 2"),
                    partWithName("image3").description("저장할 이미지 3"),
                    partWithName("image4").description("저장할 이미지 4"),
                    partWithName("image5").description("저장할 이미지 5")
                ),
                responseFields().and(
                    fieldWithPath("urls").type(JsonFieldType.ARRAY)
                        .description("이미지 URL 배열"),
                    fieldWithPath("urls[].url").type(JsonFieldType.STRING).optional()
                        .description("이미지 URL")
                )
            ));

        verify(imageService, times(1)).saveImages(any(List.class));
    }
}
