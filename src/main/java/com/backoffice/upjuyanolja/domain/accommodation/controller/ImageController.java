package com.backoffice.upjuyanolja.domain.accommodation.controller;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.ImageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/backoffice-api/images")
@RequiredArgsConstructor
@Validated
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageResponse> saveImages(
        @RequestParam(value = "image1") MultipartFile imageFile1,
        @RequestParam(value = "image2") MultipartFile imageFile2,
        @RequestParam(value = "image3") MultipartFile imageFile3,
        @RequestParam(value = "image4") MultipartFile imageFile4,
        @RequestParam(value = "image5") MultipartFile imageFile5
    ) {
        log.info("POST /backoffice-api/images");

        ImageResponse response = imageService.saveImages(List.of(
            imageFile1,
            imageFile2,
            imageFile3,
            imageFile4,
            imageFile5)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
