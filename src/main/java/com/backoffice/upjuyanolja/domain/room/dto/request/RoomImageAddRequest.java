package com.backoffice.upjuyanolja.domain.room.dto.request;

import lombok.Builder;

/**
 * 객실 이미지 추가 등록 요청 DTO Record
 *
 * @param url 등록할 객실 이미지 URL
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomImageAddRequest(String url) {

}
