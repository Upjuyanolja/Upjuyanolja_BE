package com.backoffice.upjuyanolja.domain.accommodation.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodationOwnership;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 숙소 소유권 Custom Repository Implementation
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Repository
@RequiredArgsConstructor
public class AccommodationOwnershipCustomRepositoryImpl implements
    AccommodationOwnershipCustomRepository {

    /**
     * JPA Query Factory
     */
    private final JPAQueryFactory queryFactory;

    /**
     * 숙소 소유권 QClass
     */
    private final QAccommodationOwnership qAccommodationOwnership = QAccommodationOwnership.accommodationOwnership;

    /**
     * 회원 QClass
     */
    private final QMember qMember = QMember.member;

    /**
     * 숙소 QClass
     */
    private final QAccommodation qAccommodation = QAccommodation.accommodation;

    /**
     * 회원으로 숙소 소유권 리스트를 조회하는 메서드
     * <p>
     * 숙소 소유권 테이블에 숙소, 회원 테이블을 Left Join합니다.<br> Where 절과 동적 정렬 조건을 걸어 숙소 소유권 데이터를 조회합니다.
     *
     * @param member 회원 Entity
     * @return 회원의 숙소 소유권 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public List<AccommodationOwnership> findAllByMember(Member member) {
        JPAQuery<AccommodationOwnership> query = queryFactory
            .select(qAccommodationOwnership)
            .from(qAccommodationOwnership)
            .leftJoin(qAccommodationOwnership.accommodation, qAccommodation).fetchJoin()
            .leftJoin(qAccommodationOwnership.member, qMember).fetchJoin()
            .where(createSearchConditionsBuilder(member))
            .orderBy(getAllOrderSpecifiers().toArray(OrderSpecifier[]::new));
        return query.fetch();
    }

    /**
     * 숙소 소유권 검색 조건 Builder
     *
     * @param member 회원 Entity
     * @return 숙소 소유권 검색 조건을 담은 Boolean Builder
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private BooleanBuilder createSearchConditionsBuilder(Member member) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qAccommodationOwnership.member.eq(member));
        return booleanBuilder;
    }

    /**
     * 숙소 소유권 정렬 조건을 담은 OrderSpecifier 리스트 반환 메서드
     * <p>
     * 숙소 소유권이 FK로 가지는 숙소 식별자를 기준으로 오름차순 정렬하도록 합니다.
     *
     * @return 숙소 소유권 정렬 조건을 담은 OrderSpecifier 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private List<OrderSpecifier<?>> getAllOrderSpecifiers() {
        List<OrderSpecifier<?>> orders = new LinkedList<>();
        orders.add(QueryDslUtil.getSortedColumn(
            Order.ASC,
            qAccommodationOwnership.accommodation,
            "id"
        ));
        return orders;
    }
}
