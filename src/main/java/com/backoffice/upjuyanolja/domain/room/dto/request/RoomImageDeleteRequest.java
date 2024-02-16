package com.backoffice.upjuyanolja.domain.room.dto.request;

import lombok.Builder;

/**
 * 객실 이미지 삭제 요청 DTO Record
 *
 * @param id 삭제할 객실 이미지 식별자
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomImageDeleteRequest(long id) {

}
