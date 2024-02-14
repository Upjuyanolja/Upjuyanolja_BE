package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import java.util.Optional;

/**
 * 숙소 카테고리 Custom Repository Interface
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface CategoryCustomRepository {

    /**
     * 카테고리 이름과 일치하는 하위 카테고리 Entity 조회 메서드
     *
     * @param name 조회하고자 하는 카테고리 이름
     * @return 카테고리 이름과 일치하는 하위 카테고리 Optional 객체
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    Optional<Category> findByName(String name);
}
