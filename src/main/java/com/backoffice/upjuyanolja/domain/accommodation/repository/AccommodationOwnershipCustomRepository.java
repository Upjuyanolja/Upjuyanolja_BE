package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import java.util.List;

/**
 * 숙소 소유권 Custom Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface AccommodationOwnershipCustomRepository {

    /**
     * 회원으로 숙소 소유권을 조회하는 메서드
     *
     * @param member 회원 Entity
     * @return 숙소 소유권 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    List<AccommodationOwnership> findAllByMember(Member member);
}
