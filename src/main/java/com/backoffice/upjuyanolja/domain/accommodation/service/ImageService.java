package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageUrlResponse;
import com.backoffice.upjuyanolja.domain.accommodation.exception.FailedSaveImageException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final S3UploadService s3UploadService;

    public ImageResponse saveImages(List<MultipartFile> imageMultipartFiles) {
        List<ImageUrlResponse> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : imageMultipartFiles) {
            try {
                if (multipartFile.isEmpty()) {
                    imageUrls.add(ImageUrlResponse.builder()
                        .url(null)
                        .build());
                } else {
                    imageUrls.add(ImageUrlResponse.builder()
                        .url(s3UploadService.saveFile(multipartFile))
                        .build());
                }
            } catch (IOException e) {
                throw new FailedSaveImageException();
            }
        }

        return ImageResponse.builder()
            .urls(imageUrls)
            .build();
    }
}
