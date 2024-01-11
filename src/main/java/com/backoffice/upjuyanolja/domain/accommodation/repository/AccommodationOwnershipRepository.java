package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationOwnershipRepository extends
    JpaRepository<AccommodationOwnership, Long> {

    List<AccommodationOwnership> findAllByMember(Member member);

    boolean existsAccommodationOwnershipByMemberAndAccommodation(
        Member member,
        Accommodation accommodation
    );
}
