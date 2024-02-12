package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 숙소 이미지 Repository Interface
 *
 * @author HyunA (vikim1210@naver.com)
 */
public interface AccommodationOptionRepository extends JpaRepository<AccommodationOption, Long> {

    /**
     * 숙소로 숙소 옵션을 조회하는 메서드
     *
     * @param accommodation 숙소 Entity
     * @return 숙소 옵션 Optional 객체
     * @author HyunA (vikim1210@naver.com)
     */
    Optional<AccommodationOption> findByAccommodation(Accommodation accommodation);
}
