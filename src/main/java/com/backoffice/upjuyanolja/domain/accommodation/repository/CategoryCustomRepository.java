package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import java.util.Optional;

/**
 * 숙소 카테고리 Custom Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface CategoryCustomRepository {

    Optional<Category> findCategoryByName(String name);
}
