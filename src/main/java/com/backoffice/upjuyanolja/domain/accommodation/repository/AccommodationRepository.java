package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 숙소 Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface AccommodationRepository extends JpaRepository<Accommodation, Long>,
    AccommodationCustomRepository {

}
