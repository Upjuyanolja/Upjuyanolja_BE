package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import java.util.List;

public interface AccommodationOwnershipCustomRepository {

    List<AccommodationOwnership> findAllByMember(Member member);
}
