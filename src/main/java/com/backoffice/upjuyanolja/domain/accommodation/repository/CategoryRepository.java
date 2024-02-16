package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 숙소 카테고리 Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface CategoryRepository extends JpaRepository<Category, Long>,
    CategoryCustomRepository {

}
