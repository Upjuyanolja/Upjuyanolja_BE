package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AccommodationCustomRepositoryImpl implements AccommodationCustomRepository {

    private final JPAQueryFactory query;
    private final QAccommodation qAccommodation = QAccommodation.accommodation;

    public AccommodationCustomRepositoryImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public List<Accommodation> findAllByCategoryAndName(
        String category, String keyword

    ) {
        List<Accommodation> result = getAccommodations(
            category, keyword
        );

        return result;
    }

    private List<Accommodation> getAccommodations(
        String category, String keyword
    ) {
        return query.selectFrom(qAccommodation)
            .leftJoin(qAccommodation)
            .where(
                eqCategory(category),
                eqKeyword(keyword)
            )
            .fetch();
    }

    private BooleanExpression eqCategory(String category) {
        if (category.equals("ALL")) {
            return null;
        }
        return qAccommodation.category.eq(Category.valueOf(category));
    }

    private BooleanExpression eqKeyword(String keyword) {
        if (StringUtils.isNullOrEmpty(keyword)) {
            return null;
        }
        return qAccommodation.name.eq(keyword);
    }

}
