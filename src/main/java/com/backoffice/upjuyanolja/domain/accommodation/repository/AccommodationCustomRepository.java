package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationCustomRepository {

    Page<Accommodation> searchPageByCategoryWithTypeAndName(
        String category, String keyword, Pageable pageable
    );
}
