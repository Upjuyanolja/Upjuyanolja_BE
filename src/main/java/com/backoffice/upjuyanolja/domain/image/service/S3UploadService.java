package com.backoffice.upjuyanolja.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * S3 파일 업로드 Service Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class S3UploadService {

    /**
     * AmazonS3 Interface
     */
    private final AmazonS3 amazonS3;

    /**
     * S3 버킷
     */
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일을 S3 버킷에 저장하는 메서드
     *
     * @param multipartFile 저장하고자하는 Multipart File
     * @return 저장한 후 발급받은 URL
     * @throws IOException S3 버킷에 파일을 저장하고 URL을 받아오는 과정에서 I/O 에러 발생시 예외 처리
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }
}
