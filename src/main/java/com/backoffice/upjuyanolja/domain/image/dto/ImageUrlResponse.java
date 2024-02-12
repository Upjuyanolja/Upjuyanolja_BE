package com.backoffice.upjuyanolja.domain.image.dto;

import lombok.Builder;

/**
 * 이미지 저장 URL DTO Record
 *
 * @param url 저장한 이미지 URL
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record ImageUrlResponse(String url) {

}
