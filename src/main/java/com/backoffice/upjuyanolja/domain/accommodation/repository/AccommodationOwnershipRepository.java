package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 숙소 소유권 Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface AccommodationOwnershipRepository extends
    JpaRepository<AccommodationOwnership, Long>, AccommodationOwnershipCustomRepository {

    /**
     * 숙소에 대한 회원의 소유권 여부 조회 메서드
     * <p>
     * 회원과 숙소가 일치하는 숙소 소유권 튜플의 존재 여부를 확인합니다.
     *
     * @param member        회원 Entity
     * @param accommodation 숙소 Entity
     * @return 숙소에 대한 회원의 소유권 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    boolean existsAccommodationOwnershipByMemberAndAccommodation(
        Member member,
        Accommodation accommodation
    );
}
