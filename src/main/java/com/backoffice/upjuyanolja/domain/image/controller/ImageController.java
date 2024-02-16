package com.backoffice.upjuyanolja.domain.image.controller;

import com.backoffice.upjuyanolja.domain.image.dto.ImageResponse;
import com.backoffice.upjuyanolja.domain.image.service.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * B2B Backoffice 이미지 저장 API Controller Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/backoffice-api/images")
public class ImageController {

    private final ImageService imageService;

    /**
     * 이미지 저장 API Controller
     *
     * @param imageFile1 저장하고자 하는 이미지 MultipartFile 1
     * @param imageFile2 저장하고자 하는 이미지 MultipartFile 2
     * @param imageFile3 저장하고자 하는 이미지 MultipartFile 3
     * @param imageFile4 저장하고자 하는 이미지 MultipartFile 4
     * @param imageFile5 저장하고자 하는 이미지 MultipartFile 5
     * @return 저장한 이미지 URL 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @PostMapping
    public ResponseEntity<ImageResponse> saveImages(
        @RequestParam(value = "image1") MultipartFile imageFile1,
        @RequestParam(value = "image2") MultipartFile imageFile2,
        @RequestParam(value = "image3") MultipartFile imageFile3,
        @RequestParam(value = "image4") MultipartFile imageFile4,
        @RequestParam(value = "image5") MultipartFile imageFile5
    ) {
        log.info("[POST] /backoffice-api/images");

        ImageResponse response = imageService.saveImages(List.of(
            imageFile1,
            imageFile2,
            imageFile3,
            imageFile4,
            imageFile5
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
