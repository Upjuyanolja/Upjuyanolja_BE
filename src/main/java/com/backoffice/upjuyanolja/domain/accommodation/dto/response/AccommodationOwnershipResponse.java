package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import java.util.List;
import lombok.Builder;

/**
 * 보유 숙소 목록 조회 응답 DTO Record
 *
 * @param accommodations 보유 숙소 이름 응답 DTO 리스트
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Builder
public record AccommodationOwnershipResponse(
    List<AccommodationNameResponse> accommodations
) {

}
