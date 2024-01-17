package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import java.util.List;

public interface AccommodationCustomRepository {

    List<Accommodation> findByCategoryWithTypeAndName(
        String category, String type, String keyword
    );
}
