package com.backoffice.upjuyanolja.domain.accommodation.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.ImageService;
import com.backoffice.upjuyanolja.domain.accommodation.service.S3UploadService;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private S3UploadService s3UploadService;

    @Nested
    @DisplayName("saveImages()은")
    class Context_saveImages {

        @Test
        @DisplayName("이미지를 저장할 수 있다.")
        void _willSuccess() throws IOException {
            // given
            URL resource = getClass().getResource("/images/image_sample.jpg");
            List<MultipartFile> imageFiles = new ArrayList<>();
            imageFiles.add(new MockMultipartFile(
                "image1",
                "image_sample.jpg",
                "images/png",
                new FileInputStream(resource.getFile())
            ));
            imageFiles.add(new MockMultipartFile(
                "image2",
                "image_sample.jpg",
                "images/png",
                new FileInputStream(resource.getFile())
            ));
            imageFiles.add(new MockMultipartFile(
                "image3",
                "image_sample.jpg",
                "images/png",
                new FileInputStream(resource.getFile())
            ));
            imageFiles.add(new MockMultipartFile(
                "image4",
                "image_sample.jpg",
                "images/png",
                new FileInputStream(resource.getFile())
            ));
            imageFiles.add((new MockMultipartFile(
                "image5"
                , new byte[0])
            ));

            given(s3UploadService.saveFile(any(MockMultipartFile.class)))
                .willReturn("https://aws/coupons/images/001");

            // when
            ImageResponse imageResponse = imageService.saveImages(imageFiles);

            assertThat(imageResponse.urls()).isNotEmpty();
            assertThat(imageResponse.urls().get(0).url())
                .isEqualTo("https://aws/coupons/images/001");
            assertThat(imageResponse.urls().get(1).url())
                .isEqualTo("https://aws/coupons/images/001");
            assertThat(imageResponse.urls().get(2).url())
                .isEqualTo("https://aws/coupons/images/001");
            assertThat(imageResponse.urls().get(3).url())
                .isEqualTo("https://aws/coupons/images/001");
            assertThat(imageResponse.urls().get(4).url()).isNull();
        }
    }
}
