package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccommodationCustomRepositoryImpl implements AccommodationCustomRepository {

    private final JPAQueryFactory query;
    private final QAccommodation qAccommodation = QAccommodation.accommodation;

    @Override
    public List<Accommodation> findByCategoryWithTypeAndName(
        String category, String type, String keyword

    ) {
        List<Accommodation> result = getAccommodations(
            category, category, keyword
        );

        return result;
    }

    private List<Accommodation> getAccommodations(
        String category, String type, String keyword
    ) {
        return query.selectFrom(qAccommodation)
            .leftJoin(qAccommodation)
            .where(
                eqCategory(category, category),
                eqKeyword(keyword)
            )
            .fetch();
    }

    private BooleanExpression eqCategory(String category, String type) {
        if (category.equals("ALL") && category.equals("ALL")) {
            return null;
        }
        BooleanExpression categoryExpression = qAccommodation.category.parent.name.eq(category);
        BooleanExpression typeExpression = qAccommodation.category.name.eq(type);
        if (category.equals("ALL")) {
            return typeExpression;
        }
        if (type.equals("ALL")) {
            return categoryExpression;
        }
        return categoryExpression.or(typeExpression);
    }

    private BooleanExpression eqKeyword(String keyword) {
        if (StringUtils.isNullOrEmpty(keyword)) {
            return null;
        }
        return qAccommodation.name.like("%" + keyword + "%");
    }

}
