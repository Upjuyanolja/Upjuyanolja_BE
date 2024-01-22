package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccommodationCustomRepositoryImpl implements AccommodationCustomRepository {

    private final JPAQueryFactory query;
    private final QAccommodation qAccommodation = QAccommodation.accommodation;

    @Override
    public Page<Accommodation> searchPageByCategoryWithTypeAndName(
        String category, String type, String keyword, Pageable pageable
    ) {
        Page<Accommodation> result = getAccommodations(
            category, type, keyword, pageable
        );

        return result;
    }

    private Page<Accommodation> getAccommodations(
        String category, String type, String keyword, Pageable pageable
    ) {
        List<Accommodation> content = query.selectFrom(qAccommodation)
            .leftJoin(qAccommodation)
            .where(
                eqCategory(category, type),
                eqKeyword(keyword)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = getCountQuery(category, type, keyword);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchFirst());
    }

    private JPAQuery<Long> getCountQuery(
        String category, String type, String keyword
    ) {
        return query.select(qAccommodation.count())
            .from(qAccommodation)
            .where(
                eqCategory(category, type),
                eqKeyword(keyword)
            );
    }

    private BooleanExpression eqCategory(String category, String type) {
        if (category.equals("ALL") && type.equals("ALL")) {
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
