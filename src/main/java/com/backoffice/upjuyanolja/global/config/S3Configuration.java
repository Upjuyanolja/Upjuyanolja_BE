package com.backoffice.upjuyanolja.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Amazon S3 활용을 위한 설정 Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Configuration
public class S3Configuration {

    /**
     * S3 Access key
     */
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    /**
     * S3 Secret Key
     */
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    /**
     * S3 Region
     */
    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * 설정 파일에 적혀 있는 S3 관련 설정 정보를 기반으로 AmazonS3Client 객체를 생성해서 Bean으로 등록하는 메서드
     *
     * @return AmazonS3Client
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
    }
}
