package com.backoffice.upjuyanolja.domain.image.service;

import com.backoffice.upjuyanolja.domain.image.dto.ImageResponse;
import com.backoffice.upjuyanolja.domain.image.dto.ImageUrlResponse;
import com.backoffice.upjuyanolja.domain.image.exception.FailedSaveImageException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 저장 Service Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    /**
     * S3 업로드 Service Class
     */
    private final S3UploadService s3UploadService;

    /**
     * Multipart File을 저장하고 URL을 받아 응답하는 메서드
     *
     * @param imageMultipartFiles 저장하고자 하는 이미지 Multipart File
     * @return 이미지 저장 응답 DTO 리스트
     * @throws FailedSaveImageException 이미지 저장 실패 시 예외 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Transactional
    public ImageResponse saveImages(List<MultipartFile> imageMultipartFiles) {
        List<ImageUrlResponse> imageUrlResponses = new ArrayList<>();

        for (MultipartFile multipartFile : imageMultipartFiles) {
            try {
                if (multipartFile.isEmpty()) {
                    imageUrlResponses.add(ImageUrlResponse.builder()
                        .url(null)
                        .build());
                } else {
                    imageUrlResponses.add(ImageUrlResponse.builder()
                        .url(s3UploadService.saveFile(multipartFile))
                        .build());
                }
            } catch (IOException e) {
                throw new FailedSaveImageException();
            }
        }

        return ImageResponse.builder()
            .urls(imageUrlResponses)
            .build();
    }
}
