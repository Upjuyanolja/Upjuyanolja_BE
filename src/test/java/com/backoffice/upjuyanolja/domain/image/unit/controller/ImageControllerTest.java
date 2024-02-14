package com.backoffice.upjuyanolja.domain.image.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.image.controller.ImageController;
import com.backoffice.upjuyanolja.domain.image.dto.ImageResponse;
import com.backoffice.upjuyanolja.domain.image.dto.ImageUrlResponse;
import com.backoffice.upjuyanolja.domain.image.service.ImageService;
import com.backoffice.upjuyanolja.global.security.AuthenticationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(value = ImageController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ImageControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ImageService imageService;

    @Nested
    @DisplayName("saveImage()은")
    class Context_saveImage {

        @Test
        @DisplayName("이미지를 저장할 수 있다.")
        void _willSuccess() throws Exception {
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
                        "image5"
                        , new byte[0])
                    ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.urls").isArray())
                .andExpect(jsonPath("$.urls[0].url").isString())
                .andExpect(jsonPath("$.urls[1].url").isString())
                .andExpect(jsonPath("$.urls[2].url").isString())
                .andExpect(jsonPath("$.urls[3].url").isString())
                .andExpect(jsonPath("$.urls[4].url").doesNotExist())
                .andDo(print());

            verify(imageService, times(1)).saveImages(any(List.class));
        }
    }
}
