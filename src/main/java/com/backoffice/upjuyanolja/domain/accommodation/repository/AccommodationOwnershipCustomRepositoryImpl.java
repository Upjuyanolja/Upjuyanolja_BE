package com.backoffice.upjuyanolja.domain.accommodation.repository;

import static com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation.accommodation;
import static com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodationOwnership.accommodationOwnership;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.entity.QMember;
import com.backoffice.upjuyanolja.global.util.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AccommodationOwnershipCustomRepositoryImpl implements
    AccommodationOwnershipCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QMember qMember = QMember.member;

    AccommodationOwnershipCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public List<AccommodationOwnership> findAllByMember(Member member) {
        JPAQuery<AccommodationOwnership> query = queryFactory
            .select(accommodationOwnership)
            .from(accommodationOwnership)
            .leftJoin(accommodationOwnership.accommodation, accommodation).fetchJoin()
            .leftJoin(accommodationOwnership.member, qMember).fetchJoin()
            .where(createSearchConditionsBuilder(member))
            .orderBy(getAllOrderSpecifiers().toArray(OrderSpecifier[]::new));
        return query.fetch();
    }

    private BooleanBuilder createSearchConditionsBuilder(Member member) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(accommodationOwnership.member.eq(member));
        return booleanBuilder;
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers() {
        List<OrderSpecifier<?>> orders = new LinkedList<>();
        orders.add(QueryDslUtil.getSortedColumn(
            Order.ASC,
            accommodationOwnership.accommodation,
            "id"
        ));
        return orders;
    }
}
