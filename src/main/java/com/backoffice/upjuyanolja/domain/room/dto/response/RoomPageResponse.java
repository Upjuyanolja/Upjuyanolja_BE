package com.backoffice.upjuyanolja.domain.room.dto.response;

import java.util.List;
import lombok.Builder;

/**
 * 객실 목록 조회 페이지네이션 응답 DTO Record
 *
 * @param pageNum       조회한 페이지 번호
 * @param pageSize      한 페이지에 조회한 객실 건수
 * @param totalPages    총 페이지 개수
 * @param totalElements 총 객실 건수
 * @param isLast        조회한 페이지의 마지막 페이지 여부
 * @param rooms         조회한 객실 정보 DTO 리스트
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record RoomPageResponse(
    int pageNum,
    int pageSize,
    int totalPages,
    long totalElements,
    boolean isLast,
    List<RoomsInfoResponse> rooms
) {

}
