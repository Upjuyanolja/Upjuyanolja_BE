package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationOptionRepository extends JpaRepository<AccommodationOption, Long> {

    Optional<AccommodationOption> findByAccommodation(Accommodation accommodation);

}
