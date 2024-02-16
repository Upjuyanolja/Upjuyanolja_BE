package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 숙소 이미지 Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface AccommodationImageRepository extends JpaRepository<AccommodationImage, Long> {

    /**
     * 숙소로 숙소 이미지 리스트를 조회하는 메서드
     *
     * @param accommodation 숙소 Entity
     * @return 숙소 이미지 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    List<AccommodationImage> findAllByAccommodation(Accommodation accommodation);
}
