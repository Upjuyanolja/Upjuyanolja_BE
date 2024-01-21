package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationOwnershipRepository extends
    JpaRepository<AccommodationOwnership, Long>, AccommodationOwnershipCustomRepository {

    boolean existsAccommodationOwnershipByMemberAndAccommodation(
        Member member,
        Accommodation accommodation
    );
}
