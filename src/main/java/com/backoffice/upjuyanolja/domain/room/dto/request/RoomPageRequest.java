package com.backoffice.upjuyanolja.domain.room.dto.request;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;

/**
 * 객실 페이지네이션 요청 DTO Record
 *
 * @param pageNum  조회할 페이지 번호
 * @param pageSize 한 페이지에 조회할 객실 건수
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomPageRequest(int pageNum, int pageSize) {

    /**
     * 객실 페이지네이션 요청 DTO를 생성하는 정적 팩토리 메서드
     *
     * @return 페이지네이션을 위한 PageRequest 객체
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public PageRequest of() {
        return PageRequest.of(pageNum, pageSize);
    }
}
