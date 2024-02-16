package com.backoffice.upjuyanolja.domain.accommodation.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;

/**
 * 숙소 생성, 수정, 삭제 UseCase Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface AccommodationCommandUseCase {

    /**
     * 숙소 생성 메서드
     *
     * @param memberId                     숙소를 생성하는 업주 회원 식별자
     * @param accommodationRegisterRequest 숙소 등록 요청 DTO
     * @return 숙소 정보 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    AccommodationInfoResponse createAccommodation(
        long memberId,
        AccommodationRegisterRequest accommodationRegisterRequest
    );
}
