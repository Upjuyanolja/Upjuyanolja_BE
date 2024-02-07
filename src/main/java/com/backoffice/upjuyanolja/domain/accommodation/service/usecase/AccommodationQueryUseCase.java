package com.backoffice.upjuyanolja.domain.accommodation.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

public interface AccommodationQueryUseCase {

    AccommodationPageResponse findAccommodations(
        String Category,
        boolean onlyHasCoupon,
        String keyword,
        Pageable pageable
    );

    AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId,
        LocalDate startDate,
        LocalDate endDate
    );

    AccommodationOwnershipResponse getAccommodationOwnership(long memberId);
}
