package com.backoffice.upjuyanolja.domain.accommodation.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

/**
 * 숙소 조회 UseCase Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 * @author HyunA (vikim1210@naver.com)
 */
public interface AccommodationQueryUseCase {

    AccommodationPageResponse findAccommodations(
        String category,
        boolean onlyHasCoupon,
        String keyword,
        Pageable pageable
    );

    AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * 업주 회원이 보유한 숙소 목록 조회 메서드
     *
     * @param memberId 보유 숙소 목록을 조회할 업주 회원 식별자
     * @return 업주가 보유한 숙소 이름 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    AccommodationOwnershipResponse getAccommodationOwnership(long memberId);
}
