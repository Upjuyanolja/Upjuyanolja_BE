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
        String category, String keyword, Pageable pageable
    ) {
        Page<Accommodation> result = getAccommodations(
            category, keyword, pageable
        );

        return result;
    }

    private Page<Accommodation> getAccommodations(
        String category, String keyword, Pageable pageable
    ) {
        List<Accommodation> content = query.selectFrom(qAccommodation)
            .leftJoin(qAccommodation)
            .where(
                eqCategory(category),
                eqKeyword(keyword)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = getCountQuery(category, keyword);

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchFirst());
    }

    private JPAQuery<Long> getCountQuery(
        String category, String keyword
    ) {
        return query.select(qAccommodation.count())
            .from(qAccommodation)
            .where(
                eqCategory(category),
                eqKeyword(keyword)
            );
    }

    private BooleanExpression eqCategory(String category) {
        if (category.equals("ALL")) {
            return null;
        }
        return qAccommodation.category.parent.name.eq(category);
    }

    private BooleanExpression eqKeyword(String keyword) {
        if (StringUtils.isNullOrEmpty(keyword)) {
            return null;
        }
        return qAccommodation.name.like("%" + keyword + "%");
    }

}
