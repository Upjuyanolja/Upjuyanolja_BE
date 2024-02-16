package com.backoffice.upjuyanolja.domain.image.dto;

import java.util.List;
import lombok.Builder;

/**
 * 이미지 저장 응답 DTO Record
 *
 * @param urls 이미지 저장 URL 응답 DTO 리스트
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record ImageResponse(
    List<ImageUrlResponse> urls
) {

}
