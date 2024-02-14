package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation;
import com.backoffice.upjuyanolja.domain.room.entity.QRoom;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.util.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

/**
 * 객실 Custom Repository Implementation
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Repository
@RequiredArgsConstructor
public class RoomCustomRepositoryImpl implements RoomCustomRepository {

    /**
     * JPA Query Factory
     */
    private final JPAQueryFactory queryFactory;

    /**
     * 숙소 QClass
     */
    private final QAccommodation qAccommodation = QAccommodation.accommodation;

    /**
     * 객실 QClass
     */
    private final QRoom qRoom = QRoom.room;

    /**
     * 숙소 식별자와 Pageable 객체를 받아 해당 숙소 소속의 객실 목록 조회 메서드
     *
     * @param accommodationId 객실 목록을 조회하고자 하는 숙소 식별자
     * @param pageable        페이지네이션 조건을 담은 Pageable 객체
     * @return 객실 목록 페이지
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public Page<Room> findAllByAccommodationId(long accommodationId, Pageable pageable) {
        JPAQuery<Room> query = queryFactory
            .selectFrom(qRoom)
            .leftJoin(qRoom.accommodation, qAccommodation).fetchJoin()
            .where(createSearchConditionsBuilder(accommodationId))
            .offset(pageable.getOffset())
            .orderBy(getAllOrderSpecifiers().toArray(OrderSpecifier[]::new))
            .limit(pageable.getPageSize());
        List<Room> content = query.fetch();
        return PageableExecutionUtils.getPage(
            content,
            pageable,
            () -> countByAccommodationId(accommodationId));
    }

    /**
     * 숙소 식별자로 해당 숙소 소속의 객실 목록을 조회하는 메서드
     *
     * @param accommodationId 객실 목록을 조회하고자 하는 숙소 식별자
     * @return 객실 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public List<Room> findAllByAccommodationId(long accommodationId) {
        JPAQuery<Room> query = queryFactory
            .selectFrom(qRoom)
            .leftJoin(qRoom.accommodation, qAccommodation).fetchJoin()
            .where(createSearchConditionsBuilder(accommodationId))
            .orderBy(getAllOrderSpecifiers().toArray(OrderSpecifier[]::new));
        return query.fetch();
    }

    /**
     * 숙소 내 객실 이름 중복 여부 확인 메서드
     *
     * @param name          중복 여부를 확인할 객실 이름
     * @param accommodation 중복 여부를 확인할 객실이 속한 숙소 Entity
     * @return 숙소 내 객실 이름 중복 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public boolean existsRoomByNameAndAccommodation(String name, Accommodation accommodation) {
        Integer fetchOne = queryFactory
            .selectOne()
            .from(qRoom)
            .where(createExistsSearchConditionsBuilder(name, accommodation))
            .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 숙소에 속한 객실 개수 조회 메서드
     *
     * @param accommodationId 객실 개수를 조회할 숙소 식별자
     * @return 객실 개수
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public long countByAccommodationId(long accommodationId) {
        JPAQuery<Room> countQuery = queryFactory
            .selectFrom(qRoom)
            .leftJoin(qRoom.accommodation, qAccommodation).fetchJoin()
            .where(createSearchConditionsBuilder(accommodationId));
        return countQuery.fetch().size();
    }

    /**
     * 객실 목록 조회 조건 Builder 생성 메서드
     *
     * @param accommodationId 객실 목록 조회할 숙소 식별자
     * @return 객실 목록 조회 조건을 담은 Boolean Builder
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private BooleanBuilder createSearchConditionsBuilder(long accommodationId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qRoom.accommodation.id.eq(accommodationId));
        booleanBuilder.and(qRoom.deletedAt.isNull());
        return booleanBuilder;
    }

    /**
     * 숙소 내 객실 이름 중복 여부 확인 조건 Builder 생성 메서드
     *
     * @param name          중복 여부를 확인할 객실 이름
     * @param accommodation 객실 이름 중복 여부를 확인할 숙소 Entity
     * @return 숙소 내 객실 이름 중복 여부 확인 조건을 담은 Boolean Builder
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private BooleanBuilder createExistsSearchConditionsBuilder(
        String name,
        Accommodation accommodation
    ) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qRoom.name.eq(name));
        booleanBuilder.and(qRoom.accommodation.eq(accommodation));
        booleanBuilder.and(qRoom.deletedAt.isNull());
        return booleanBuilder;
    }

    /**
     * 객실 목록 정렬 조건을 담은 OrderSpecifier 리스트 반환 메서드
     * <p>
     * 1. 객실 상태를 기준으로 오름차순 정렬하도록 합니다.<br> 2. 객실 식별자를 기준으로 오름차순 정렬하도록 합니다.
     *
     * @return 객실 목록 정렬 조건을 담은 OrderSpecifier 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private List<OrderSpecifier<?>> getAllOrderSpecifiers() {
        List<OrderSpecifier<?>> orders = new LinkedList<>();
        orders.add(QueryDslUtil.getSortedColumn(Order.ASC, qRoom, "status"));
        orders.add(QueryDslUtil.getSortedColumn(Order.ASC, qRoom, "id"));
        return orders;
    }
}
