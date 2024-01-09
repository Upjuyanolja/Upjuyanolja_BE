package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationOwnershipRepository extends
    JpaRepository<AccommodationOwnership, Long> {

}
