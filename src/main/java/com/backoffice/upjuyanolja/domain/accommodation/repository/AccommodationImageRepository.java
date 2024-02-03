package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {

    List<AccommodationImage> findByAccommodation(Accommodation accommodation);

}
