package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 숙소 카테고리 Custom Repository Implementation
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Repository
@RequiredArgsConstructor
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {

    /**
     * JPA Query Factory
     */
    private final JPAQueryFactory queryFactory;

    /**
     * 숙소 카테고리 QClass
     */
    private final QCategory qCategory = QCategory.category;

    /**
     * 숙소 상위 카테고리 개수
     */
    private static final long NUMBER_OF_TOP_CATEGORY = 4L;

    /**
     * 카테고리 이름과 일치하는 하위 카테고리 Entity 조회 메서드
     *
     * @param name 조회하고자 하는 카테고리 이름
     * @return 카테고리 이름과 일치하는 하위 카테고리 Optional 객체
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public Optional<Category> findByName(String name) {
        JPAQuery<Category> query = queryFactory
            .selectFrom(qCategory)
            .where(createSearchConditionsBuilder(name));
        return Optional.ofNullable(query.fetchFirst());
    }

    /**
     * 숙소 카테고리 검색 조건 Builder
     *
     * @return 숙소 카테고리 검색 조건을 담은 Boolean Builder
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private BooleanBuilder createSearchConditionsBuilder(String name) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qCategory.name.eq(name));
        booleanBuilder.and(qCategory.id.gt(NUMBER_OF_TOP_CATEGORY));
        return booleanBuilder;
    }
}
